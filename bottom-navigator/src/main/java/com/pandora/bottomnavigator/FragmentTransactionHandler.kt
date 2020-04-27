/*
 * Copyright 2019 Pandora Media, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See accompanying LICENSE file or you may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pandora.bottomnavigator

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.pandora.bottomnavigator.FragmentTransactionCommand.AddAndShow
import com.pandora.bottomnavigator.FragmentTransactionCommand.Clear
import com.pandora.bottomnavigator.FragmentTransactionCommand.RemoveAllAndAdd
import com.pandora.bottomnavigator.FragmentTransactionCommand.RemoveAllAndShowExisting
import com.pandora.bottomnavigator.FragmentTransactionCommand.ShowAndRemove
import com.pandora.bottomnavigator.FragmentTransactionCommand.ShowExisting
import java.util.UUID

internal data class CommandWithRunnable(
    val command: FragmentTransactionCommand,
    val runAfterCommit: () -> Unit
)

internal sealed class FragmentTransactionCommand {
    data class AddAndShow(val fragment: Fragment, val tag: TagStructure) : FragmentTransactionCommand()
    data class ShowExisting(val tag: TagStructure) : FragmentTransactionCommand()
    data class ShowAndRemove(val showTag: TagStructure, val removeTag: TagStructure) : FragmentTransactionCommand()
    data class Clear(val allCurrentTags: List<TagStructure>) : FragmentTransactionCommand()
    data class RemoveAllAndAdd(val remove: List<TagStructure>, val add: AddAndShow) : FragmentTransactionCommand()
    data class RemoveAllAndShowExisting(val remove: List<TagStructure>, val show: ShowExisting) : FragmentTransactionCommand()
    data class RemoveUnknown(val knownFragments: List<TagStructure>): FragmentTransactionCommand()
}

internal class FragmentTransactionHandler(
    private val fm: FragmentManager,
    @IdRes private val container: Int
) {
    fun handle(commandWithRunnable: CommandWithRunnable) {
        val (command, runnable) = commandWithRunnable

        @Suppress("UNUSED_VARIABLE") // val exhaustWhen is just used to help us remember to add the when if a new command is added
        val exhaustWhen = when (command) {
            is AddAndShow -> addAndShowFragment(command.fragment, command.tag, runnable)
            is ShowExisting -> showFragment(command.tag, runnable)
            is ShowAndRemove -> showAndRemoveFragment(
                command.showTag,
                command.removeTag,
                runnable
            )
            is Clear -> clear(runnable)
            is RemoveAllAndAdd -> removeAllAndAdd(command.remove, command.add.fragment, command.add.tag, runnable)
            is RemoveAllAndShowExisting -> removeAllAndShow(command.remove, command.show.tag, runnable)
            is FragmentTransactionCommand.RemoveUnknown -> removeUnknown(command, runnable)
        }
    }

    private fun removeAllAndAdd(
        remove: List<TagStructure>,
        add: Fragment, addTag: TagStructure,
        runnable: () -> Unit
    ) {
        val transaction = fm.beginTransaction()
        for (removeTag in remove) {
            val removeFragment = fm.findFragmentByTag(removeTag.toString())
            if (removeFragment != null) {
                transaction.remove(removeFragment)
            }
        }
        transaction.add(container, add, addTag.toString())
            .detachOtherFragments(add)
            .runOnCommit(runnable)
            .setReorderingAllowed(true)
            .commitNow()
    }

    private fun removeAllAndShow(
        remove: List<TagStructure>,
        show: TagStructure,
        runnable: () -> Unit
    ) {
        val transaction = fm.beginTransaction()
        for (removeTag in remove) {
            val removeFragment = fm.findFragmentByTag(removeTag.toString())
            if (removeFragment != null) {
                transaction.remove(removeFragment)
            }
        }
        val fragment = fm.findFragmentByTag(show.toString())!!
        transaction.showOrAttach(fragment)
            .detachOtherFragments(fragment)
            .runOnCommit(runnable)
            .setReorderingAllowed(true)
            .commitNow()
    }

    private fun showAndRemoveFragment(
        showTag: TagStructure,
        removeTag: TagStructure,
        runnable: () -> Unit
    ) {
        val showFragment = fm.findFragmentByTag(showTag.toString())!!
        val removeFragment = fm.findFragmentByTag(removeTag.toString())!!
        fm.beginTransaction().apply {
            removeTag.transitionsData?.let { setCustomAnimations(it.popEnterAnim, it.popExitAnim) }
            remove(removeFragment)
            detachOtherFragments(showFragment)
            showOrAttach(showFragment)
            runOnCommit(runnable)
            setReorderingAllowed(true)
            commitNow()
        }
    }

    private fun showFragment(
        tag: TagStructure,
        runnable: () -> Unit
    ) {
        val fragment = fm.findFragmentByTag(tag.toString())!!
        fm.beginTransaction()
            .showOrAttach(fragment)
            .detachOtherFragments(fragment)
            .runOnCommit(runnable)
            .setReorderingAllowed(true)
            .commitNow()
    }

    private fun FragmentTransaction.showOrAttach(fragment: Fragment): FragmentTransaction {
        return if (TagStructure.fromTag(fragment.tag!!).isDetachable) {
            attach(fragment)
        } else {
            show(fragment)
        }
    }

    private fun addAndShowFragment(
        fragment: Fragment,
        tag: TagStructure,
        runnable: () -> Unit
    ) {
        fm.beginTransaction().apply {
            tag.transitionsData?.let { setCustomAnimations(it.enterAnim, it.exitAnim) }
            add(container, fragment, tag.toString())
            detachOtherFragments(fragment)
            runOnCommit(runnable)
            setReorderingAllowed(true)
            commitNow()
        }
    }

    private fun FragmentTransaction.detachOtherFragments(keep: Fragment): FragmentTransaction {
        return fm.fragments
            .filter { it != keep && TagStructure.fromTag(it.tag).isOurFragment }
            .fold(this) { transaction, fragment ->
                if (TagStructure.fromTag(fragment.tag!!).isDetachable) {
                    transaction.detach(fragment)
                } else {
                    transaction.hide(fragment)
                }
            }
    }

    private fun clear(runnable: () -> Unit) {
        fm.fragments
            .filter { TagStructure.fromTag(it.tag).isOurFragment }
            .fold(fm.beginTransaction()) { transaction, fragment ->
                transaction.remove(fragment)
            }
            .runOnCommit(runnable)
            .setReorderingAllowed(true)
            .commitNow()
    }

    private fun removeUnknown(
        command: FragmentTransactionCommand.RemoveUnknown,
        runnable: () -> Unit
    ) {
        val knownFragments = command.knownFragments

        val unknown = fm.fragments
            .filter {
                val tag = TagStructure.fromTag(it.tag)

                // it's our fragment but we don't know about it
                tag.isOurFragment && !knownFragments.contains(tag)
            }

        if (unknown.isNotEmpty()) {
            unknown
                .fold(fm.beginTransaction()) { transaction, fragment ->
                    transaction.remove(fragment)
                }
                .runOnCommit(runnable)
                .setReorderingAllowed(true)
                .commitNow()
        }
    }

}

/**
 * Info that gets serialized into the FragmentManager's fragment tag string
 */
@Suppress("DataClassPrivateConstructor")
internal data class TagStructure private constructor(
    val className: String?,
    val detachable: Boolean?,
    val uuid: String?,
    val transitionsData: TransitionsData?
) {
    constructor(fragment: Fragment, detachable: Boolean, transitionsData: TransitionsData? = null) :
            this(fragment::class.java.name, detachable, UUID.randomUUID().toString(), transitionsData)

    var isOurFragment = true
        private set
    var isDetachable = detachable == true

    override fun toString(): String {
        return StringBuilder()
            .append(OURTAG, SEPARATOR
                , className, SEPARATOR
                , if (detachable == true) DETACHABLE else "", SEPARATOR
                , uuid, SEPARATOR
                , transitionsData ?: "")
            .toString()
    }

    companion object {
        /**
         * Used to identify fragments we've added to the fragment manager
         */
        private const val OURTAG = "com.pandora.navigator"
        private const val SEPARATOR = "|"
        private const val DETACHABLE = "DETACHABLE"

        fun fromTag(tag: String?): TagStructure {
            if (tag == null || !tag.startsWith(OURTAG)) {
                return TagStructure(null, null, null, null)
                    .apply { isOurFragment = false }
            }

            val (ourTag, className, detachable, uuid, transitionsData) = tag.split(SEPARATOR)

            if (ourTag != OURTAG) return TagStructure(null, null, null, null)
                .apply { isOurFragment = false }

            return TagStructure(className, DETACHABLE == detachable, uuid, TransitionsData.fromTag(transitionsData))
        }
    }

    /**
     * Data class for a fragment's transitions. Responsible for serializing and de-serializing itself
     */
    internal data class TransitionsData(
        val enterAnim: Int,
        val exitAnim: Int,
        val popEnterAnim: Int,
        val popExitAnim: Int
    ) {
        override fun toString(): String {
            return StringBuilder()
                .append(
                    enterAnim, TRANSITIONS_SEPARATOR
                    , exitAnim, TRANSITIONS_SEPARATOR
                    , popEnterAnim, TRANSITIONS_SEPARATOR
                    , popExitAnim
                ).toString()
        }

        companion object {
            const val TRANSITIONS_SEPARATOR = "."

            /**
             * Returns a transition object if the string contains the transitions separator, or null
             */
            fun fromTag(tag: String): TransitionsData? {
                return if (tag.contains(TRANSITIONS_SEPARATOR)) {
                    val (enterAnim, exitAnim, popEnterAnim, popExitAnim) = tag.split(TRANSITIONS_SEPARATOR)
                    TransitionsData(
                        enterAnim.toInt(),
                        exitAnim.toInt(),
                        popEnterAnim.toInt(),
                        popExitAnim.toInt()
                    )
                } else null
            }
        }
    }
}
