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
package androidx.fragment.app

import android.os.Bundle
import android.view.View
import java.util.ArrayList

class FragmentState(var fragment: Fragment, var attached: Boolean, var shown: Boolean,
                    var executedEnterAnim: Int, var executedExitAnim: Int)

open class FakeFragmentManager : FragmentManager() {
    val map = mutableMapOf<String?, FragmentState>()
    fun attachedFragments() = map.values.filter { it.attached }.map { it.fragment }
    fun detachedFragments() = map.values.filter { !it.attached }.map { it.fragment }
    fun shownFragments() = map.values.filter { it.shown }.map { it.fragment }
    fun hiddenFragments() = map.values.filter { !it.shown }.map { it.fragment }
    fun executedTransitions(tag: String) = map[tag]?.let { Pair(it.executedEnterAnim, it.executedExitAnim) }

    override fun beginTransaction(): FragmentTransaction {
        return FakeFragmentTransaction(map)
    }

    override fun findFragmentByTag(tag: String?) = map[tag]?.fragment

    override fun getFragments() = map.values.map { it.fragment }

    override fun findFragmentById(id: Int): Fragment? {
        TODO(
            "not implemented"
        )
    }

    override fun putFragment(bundle: Bundle, key: String, fragment: Fragment) {}

    override fun removeOnBackStackChangedListener(listener: OnBackStackChangedListener) {}

    override fun getFragment(bundle: Bundle, key: String) = null

    override fun unregisterFragmentLifecycleCallbacks(cb: FragmentLifecycleCallbacks) {}

    override fun getPrimaryNavigationFragment(): Fragment? = null

    override fun getBackStackEntryCount() = 0

    override fun isDestroyed() = false

    override fun getBackStackEntryAt(index: Int): BackStackEntry {
        TODO(
            "not implemented"
        )
    }

    override fun executePendingTransactions() = false

    override fun popBackStackImmediate() = false

    override fun popBackStackImmediate(name: String?, flags: Int) = false

    override fun popBackStackImmediate(id: Int, flags: Int) = false

    override fun addOnBackStackChangedListener(listener: OnBackStackChangedListener) {}

    override fun isStateSaved() = false

    override fun popBackStack() {}

    override fun popBackStack(name: String?, flags: Int) {}

    override fun popBackStack(id: Int, flags: Int) {}

    override fun registerFragmentLifecycleCallbacks(
        cb: FragmentLifecycleCallbacks, recursive: Boolean
    ) {
    }
}

class FakeFragmentTransaction(private val original: MutableMap<String?, FragmentState>) :
    FragmentTransaction() {
    private var mCommitRunnable: ArrayList<Runnable>? = null
    private val copy = HashMap(original)

    private fun findFragmentKey(fragment: Fragment): String? {
        return copy.filterValues { it.fragment == fragment }.keys.firstOrNull()
    }

    override fun remove(fragment: Fragment): FragmentTransaction {
        copy.remove(findFragmentKey(fragment))
        return this
    }

    override fun add(fragment: Fragment, tag: String?): FragmentTransaction {
        copy[tag] = FragmentState(fragment, true, true, mEnterAnim, mExitAnim)
        fragment.mTag = tag
        return this
    }

    override fun add(containerViewId: Int, fragment: Fragment): FragmentTransaction {
        copy[null] = FragmentState(fragment, true, true, mEnterAnim, mExitAnim)
        return this
    }

    override fun add(containerViewId: Int, fragment: Fragment, tag: String?): FragmentTransaction {
        copy[tag] = FragmentState(fragment, true, true, mEnterAnim, mExitAnim)
        fragment.mTag = tag
        return this
    }

    override fun hide(fragment: Fragment): FragmentTransaction {
        copy[findFragmentKey(fragment)]?.shown = false
        return this
    }

    override fun replace(containerViewId: Int, fragment: Fragment): FragmentTransaction {
        copy.clear()
        copy[null] = FragmentState(fragment, true, true, 0, 0)
        return this
    }

    override fun replace(
        containerViewId: Int, fragment: Fragment, tag: String?
    ): FragmentTransaction {
        copy.clear()
        copy[tag] = FragmentState(fragment, true, true, 0, 0)
        return this
    }

    override fun setBreadCrumbShortTitle(res: Int) = this

    override fun setBreadCrumbShortTitle(text: CharSequence?) = this

    override fun commit(): Int {
        original.clear()
        original.putAll(copy)

        mCommitRunnable?.forEach { it.run() }

        return 1
    }

    override fun setPrimaryNavigationFragment(fragment: Fragment?) = this

    override fun runOnCommit(runnable: Runnable): FragmentTransaction {
        if (mCommitRunnable == null) {
            mCommitRunnable = ArrayList()
        }
        mCommitRunnable?.add(runnable)

        return this
    }

    override fun detach(fragment: Fragment): FragmentTransaction {
        copy[findFragmentKey(fragment)]?.attached = false
        return this
    }

    override fun commitAllowingStateLoss() = commit()

    override fun setAllowOptimization(allowOptimization: Boolean) = this

    override fun addToBackStack(name: String?) = this

    override fun disallowAddToBackStack() = this

    override fun setTransitionStyle(styleRes: Int) = this

    override fun setTransition(transit: Int) = this

    override fun attach(fragment: Fragment): FragmentTransaction {
        copy[findFragmentKey(fragment)]?.apply {
            executedEnterAnim = mEnterAnim
            executedExitAnim = mExitAnim
            attached = true
        }
        return this
    }

    override fun show(fragment: Fragment): FragmentTransaction {
        copy[findFragmentKey(fragment)]?.apply {
            executedEnterAnim = mEnterAnim
            executedExitAnim = mExitAnim
            shown = true
        }
        return this
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun isAddToBackStackAllowed(): Boolean {
        return true
    }

    override fun addSharedElement(sharedElement: View, name: String) = this

    override fun commitNow() {
        commit()
    }

    override fun setBreadCrumbTitle(res: Int) = this

    override fun setBreadCrumbTitle(text: CharSequence?) = this

    override fun setReorderingAllowed(reorderingAllowed: Boolean) = this

    override fun commitNowAllowingStateLoss() {
        commit()
    }
}