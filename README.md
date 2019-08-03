# Bottom Navigator

Bottom Navigator is a library for managing multiple Fragment backstacks
with a BottomNavigationView. It has a simple API and is feature-rich.

Unlike Material Design's Android recommendations, the state of each
stack is preserved as the user switches between tabs. 

The library keeps a history of previous tabs so that when the current
tab's stack is exhausted the system back button will take you to the
previously selected tab. The state of the tabs and their stacks is
automatically preserved across rotation.

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

Then in the activity's onCreate you initialize the BottomNavigator by
calling its onCreate function. 

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

Finally, wire up the backbutton to the BottomNavigator

```kotlin
    override fun onBackPressed() {
        if (!navigator.pop()) {
            super.onBackPressed()
        }
    }
```

Checkout the sample app for a complete example.


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