[![Build Status](https://travis-ci.com/PandoraMedia/BottomNavigator.svg?branch=master)](https://travis-ci.com/PandoraMedia/BottomNavigator) <a href='https://search.maven.org/search?q=g:com.pandora.bottomnavigator%20a:bottom-navigator'><img src='https://img.shields.io/maven-central/v/com.pandora.bottomnavigator/bottom-navigator.svg'></a> <img src='https://img.shields.io/github/license/pandoramedia/BottomNavigator'>

# Bottom Navigator

Bottom Navigator is a library for managing multiple Fragment backstacks
controlled by the tabs of a BottomNavigationView. It has a simple API
and is feature-rich.

Unlike Material Design's Android recommendations, the state of each
tab's stack is preserved as the user switches between tabs. 

The library keeps a history of previous tabs so that when the current
tab's stack is exhausted the system back button will navigate to the
previously selected tab. Rotation is automatically handled and all
backstacks are preserved.


##### Table of Contents  
[How to Use it](#how-to-use-it)  
[Multiple taps on a tab](#multiple-taps-on-a-tab)  
[Detachability](#detachability)  
[Obtaining a BottomNavigator reference](#obtaining-a-bottomnavigator-reference)  
[Dependencies](#dependencies)  
[Gradle](#gradle)  


## How to Use it

BottomNavigator needs to be hosted by an Activity or Fragment with a BottomNavigationView and a
child fragment container.

```xml
<LinearLayout 
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <FrameLayout
        android:id="@+id/fragment_container"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomnav_view"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        app:menu="@menu/navigation_items" />
</LinearLayout>
```

Then, in the host activity's onCreate or host fragment's onViewCreated you initialize the BottomNavigator by
calling its onCreate function. You need to provide a
`rootFragmentsFactory` that maps the BottomNavigationView's menu items
to the root fragment for each tab. And a `defaultTab` which is the first
tab shown to the user.

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator = BottomNavigator.onCreate(
            fragmentContainer = R.id.fragment_container,
            bottomNavigationView = findViewById(R.id.bottomnav_view),
            rootFragmentsFactory = mapOf(
                R.id.tab1 to { RootFragment1() },
                R.id.tab2 to { RootFragment2() },
                R.id.tab3 to { RootFragment3() }
            ),
            defaultTab = R.id.tab2,
            activity = this
        )
    }
```

Finally, wire up the back button to the BottomNavigator.

```kotlin
    override fun onBackPressed() {
        if (!navigator.pop()) {
            super.onBackPressed()
        }
    }
```

You can now call methods like `navigator.addFragment(fragment)` to add a
fragment to the current tab's stack, or `navigator.switchTab(R.id.tab1)`
to switch stacks.

Checkout the sample app for a complete example.

## Multiple taps on a tab

When a user taps on the currently shown bottom tab the expectation is
that the tab's state will be reset. If the tab has a backstack then the
stack will be removed leaving the root fragment in whatever state it was
in. Another tap when the root Fragment is shown will recreate the fragment
and replace it with the new root fragment.

In order to get a smoother user experience you can avoid recreating the
root fragment by subscribing to BottomNavigator's
`resetRootFragmentCommand()` rx stream. In the subscription to that stream
you should reset the given root fragment's state. For example, you can
call `smoothScrollToPosition(0)` on a RecyclerView in the Fragment, or
you might clear the text in a search box.

## Detachability

By default as Fragments are hidden and shown as the user navigates
around, the fragments are attached and detached which generates the
`onDestroyView`/`onCreateView` lifecycle on the Fragments. This is good
for memory consumption because it allows View objects that are not being
seen by the user to be garbage collected and then recreated in
onCreateView when the user wants to see them again.

If you do not want hidden Fragments to have their Views destroyed, maybe
because of lifecycle issues or for performance, you can mark a Fragment
as not being detachable. This will cause the fragments to be
hidden/shown without having their Views destroyed. To do this specify
`detachable = false` when adding the fragment, or for root Fragments by
initializing BottomNavigator with `onCreateWithDetachability` and
providing `FragmentInfo` objects to the `rootFragmentsFactory`.

## Obtaining a BottomNavigator reference

BottomNavigator is scoped to an activity or fragment session, this means that after
rotation a new host Activity or Fragment gets the same instance from
`BottomNavigator.onCreate` as the previous instance. Fragments
and other objects with reference to the host can obtain the same navigator instance by calling
`BottomNavigator.provide(host)`. This allows the Fragments to add
other Fragments to the BottomNavigator.

## Dependencies

BottomNavigator works with AndroidX apps. It is built in Kotlin on top
of Architecture Components and RxJava.

## Gradle 

```groovy
dependencies {
    implementation 'com.pandora.bottomnavigator:bottom-navigator:1.8'
}
```

## Further reading

https://engineering.pandora.com/announcing-bottom-navigator-64f6e426a6b1

## License
```
Copyright 2019 Pandora Media, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
See accompanying LICENSE file or you may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
