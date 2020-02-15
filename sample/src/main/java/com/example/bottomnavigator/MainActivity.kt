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
package com.example.bottomnavigator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pandora.bottomnavigator.BottomNavigator
import com.pandora.bottomnavigator.NavigatorAction
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {
    private lateinit var navigator: BottomNavigator
    private var disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator = BottomNavigator.onCreate(
            fragmentContainer = R.id.fragment_container,
            bottomNavigationView = findViewById(R.id.bottomnav_view),
            rootFragmentsFactory = mapOf(
                R.id.tab1 to { SampleFragment() },
                R.id.tab2 to { SampleFragment() },
                R.id.tab3 to { SampleFragment() }
            ),
            defaultTab = R.id.tab2,
            activity = this
        )

        disposables.add(navigator.infoStream().subscribe {
            navigator.currentFragment()?.let { it1 ->
                when (it) {
                    is NavigatorAction.FragmentRemovedWithResult -> {
                        val result = it.result
                        // Send result to current fragment
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        if (!navigator.pop()) {
            super.onBackPressed()
        }
    }

}

class SampleFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment, container, false)

        val bottomNavigator = BottomNavigator.provide(activity!!)
        val tab = when (bottomNavigator.currentTab()) {
            R.id.tab1 -> 1
            R.id.tab2 -> 2
            R.id.tab3 -> 3
            else -> -1
        }
        val depth = bottomNavigator.currentStackSize()

        rootView.findViewById<TextView>(R.id.title).text = "Tab $tab Depth $depth"


        rootView.findViewById<Button>(R.id.btn).setOnClickListener {
            bottomNavigator.addFragment(SampleFragment())
        }

        if (bottomNavigator.currentStackSize() > 1) {
            val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar)
            toolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_arrow_back_black_24dp)
            toolbar.setNavigationOnClickListener { bottomNavigator.pop() }
        }

        rootView.findViewById<Button>(R.id.btn_clear_all).setOnClickListener {
            bottomNavigator.clearAll()
        }

        return rootView
    }

}