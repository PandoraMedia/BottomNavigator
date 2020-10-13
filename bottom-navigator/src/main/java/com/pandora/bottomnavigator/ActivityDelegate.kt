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

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.disposables.CompositeDisposable

/**
 * Handles subscriptions that would traditionally be handled by the Activity.
 */
internal class ActivityDelegate(
    private val fragmentContainer: Int,
    fragmentManagerFactory: () -> FragmentManager,
    private val lifecycle: Lifecycle,
    private val bottomNavigationView: BottomNavigationView,
    private val bottomNavigator: BottomNavigator
) : LifecycleObserver {
    fun clear() {
        bin.clear()
        lifecycle.removeObserver(this)
    }

    private val bin = CompositeDisposable()
    val fragmentManager = fragmentManagerFactory()

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onActivityStart() {
        bin.clear()
        val fragmentTransactionHandler =
            FragmentTransactionHandler(fragmentManager, fragmentContainer)
        bottomNavigator.fragmentTransactionPublisher
            .subscribe { command ->
                fragmentTransactionHandler.handle(command)
            }
            .into(bin)

        setupBottomNavigationView()
    }

    private fun setupBottomNavigationView() {
        // Don't trigger onNavigationItemSelected when setSelectedItem was called programmatically.
        var programmaticSelect = false
        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (programmaticSelect) {
                programmaticSelect = false
            } else {
                bottomNavigator.onNavigationItemSelected(it)
            }
            true
        }

        bottomNavigator.bottomnavViewSetSelectedItemObservable
            .subscribe { currentTab ->
                if (bottomNavigationView.selectedItemId != currentTab) {
                    programmaticSelect = true
                    bottomNavigationView.selectedItemId = currentTab
                }
            }
            .into(bin)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onActivityStop() {
        bin.clear()
        bottomNavigationView.setOnNavigationItemSelectedListener(null)
    }
}
