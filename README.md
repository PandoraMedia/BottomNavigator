# Bottom Navigator

Bottom Navigator is a library for managing multiple Fragment backstacks
mapped to the tabs of a BottomNavigationView. It has a simple API
and is feature-rich.

Unlike Material Design's Android recommendations, the state of each
tab's stack is preserved as the user switches between tabs. 

The library keeps a history of previous tabs so that when the current
tab's stack is exhausted the system back button will navigate to the
previously selected tab. Rotation is automatically handled, all
backstacks are preserved.

## How to Use it

BottomNavigator needs an Activity with a BottomNavigationView and a
fragment container. 

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

Then, in the activity's onCreate you initialize the BottomNavigator by
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
in. If there is no backstack then the tap will cause BottomNavigator to
discard the root fragment and recreate a new root fragment, thus
discarding the previous root fragment's state.

In order to avoid recreating the root fragment to get a smoother user
experience you can subscribe to BottomNavigator's 
`resetRootFragmentCommand()` rx stream. You can then use events from
that stream to clear the root fragment's state. For example you might
call `smoothScrollToPosition(0)` on a RecyclerView in the Fragment. Or
you might clear the text in a search box.

## Detachability

By default, as Fragments are hidden and shown as the user navigates
around, the fragments are attached and detached which generates the
`onDestroyView`/`onCreateView` lifecycle on the Fragments. This is good
for memory consumption because it allows View objects that are not being
seen by the user to be garbage collected and then recreated in
onCreateView when the user wants to see them again.

If you do not want hidden Fragments to have their Views destroyed, maybe
because of lifecycle issues or for performance, you can mark a Fragment
as not being detachable. This will cause the fragments to be
hidden/shown without having their Views desroyed. To do this specify
`detachable = false` when adding the fragment, or for root Fragments by
initializing BottomNavigator with `onCreateWithDetachability` and
providing `FragmentInfo` objects to the `rootFragmentsFactory`.

## Dependencies

BottomNavigator works with AndroidX apps. It depends on rxJava2 and
Architecture Components.

## Gradle 

```groovy
repositories {
    jcenter()
}

dependencies {
    implementation 'com.pandora.bottomnavigator:$latest_version'
}
```




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