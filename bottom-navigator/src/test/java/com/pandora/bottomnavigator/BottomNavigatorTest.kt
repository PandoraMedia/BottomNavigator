package com.pandora.bottomnavigator

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FakeFragmentManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.observers.TestObserver
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BottomNavigatorTest {
    @Rule
    @JvmField
    val throwRxErrorsRule = RxErrorsRule()
    private val tab1 = 1
    private val tab2 = 2
    private val tab3 = 3
    private val tab4 = 4
    private val vmStore = ViewModelStore()
    private var fragmentManager = FakeFragmentManager()
    private lateinit var rootFragment1: Fragment
    private lateinit var rootFragment2: Fragment
    private lateinit var rootFragment3: Fragment
    private lateinit var rootFragment4: Fragment
    private val rootFragmentsFactory = mapOf(
        Pair(tab1, {
            rootFragment1 = mock { on { toString() } doReturn "rootFragment1" }
            rootFragment1
        }),
        Pair(tab2, {
            rootFragment2 = mock { on { toString() } doReturn "rootFragment2" }
            rootFragment2
        }),
        Pair(tab3, {
            rootFragment3 = mock { on { toString() } doReturn "rootFragment3" }
            rootFragment3
        }),
        Pair(tab4, {
            rootFragment4 = mock { on { toString() } doReturn "rootFragment4" }
            rootFragment4
        })
    )

    @Test
    fun initialize() {
        // Given an uninitialized bottomnav When we initialize it
        val bottomNavView = generateBottomViewMock()
        val activity1: FragmentActivity? = generateActivityMock()
        val bottomNavigator = BottomNavigator.onCreate(
            activity = activity1!!,
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        )
        // And the activity starts
        bottomNavigator.activityDelegate!!.onActivityStart()
        // Then the defaultTab's fragment is shown
        assertTrue { fragmentManager.attachedFragments()[0] == rootFragment2 }
        // And bottomnav is on tab3
        verify(bottomNavView).selectedItemId = tab2
    }

    @Test
    fun clickBottomNavToDifferentTab() {
        // Given an BottomNavigator initialized with tab2
        val bottomNavView = generateBottomViewMock()
        BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // Then bottomNavView is set to tab2
        verify(bottomNavView).selectedItemId = tab2
        // And rootFragment2 is shown
        assertTrue(fragmentManager.attachedFragments().contains(rootFragment2))
        // When we click to tab3
        clickTab(tab3)
        // Then rootFragment3 is shown
        assertTrue(fragmentManager.attachedFragments().contains(rootFragment3))
        assertTrue(fragmentManager.detachedFragments().contains(rootFragment2))
    }

    @Test
    fun clickingTabsPreservesTheStack() {
        // Given a BottomNavigator with two fragments on tab2
        val bottomNavigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = generateBottomViewMock(),
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        val frag2_2 = mock<Fragment>()
        bottomNavigator.addFragment(frag2_2)
        // When we click on tab1
        clickTab(tab1)
        // Then rootFragment1 is shown
        assertTrue(fragmentManager.attachedFragments().contains(rootFragment1))
        // And the other fragments are detached
        assertEquals(setOf(rootFragment2, frag2_2), HashSet(fragmentManager.detachedFragments()))
        // When we click back to tab2
        clickTab(tab2)
        // Then frag2_2 is shown
        assertTrue(fragmentManager.attachedFragments().contains(frag2_2))
        // And the other fragments are detached
        assertEquals(
            setOf(rootFragment1, rootFragment2),
            HashSet(fragmentManager.detachedFragments())
        )
    }

    @Test
    fun clickSameTabTwiceClearsStack3TimesScrollsUp() {
        // Given a BottomNavigator with two fragments on tab2
        val bottomNavigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = generateBottomViewMock(),
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        val frag2_2 = mock<Fragment> { on { toString() } doReturn "fragment2_2" }
        bottomNavigator.addFragment(frag2_2)
        // And two fragments on tab1
        bottomNavigator.switchTab(tab1)
        val frag1_2 = mock<Fragment> { on { toString() } doReturn "fragment1_2" }
        bottomNavigator.addFragment(frag1_2)
        // And a scrollFragmentListener
        val scrollFragmentListener = bottomNavigator.resetRootFragmentCommand().test()
        // When we click on tab 2
        clickTab(tab2)
        // Then frag2_2 is shown
        assertEquals(frag2_2, fragmentManager.attachedFragments()[0])
        // When we click on tab2 a second time
        clickTab(tab2)
        // Then rootFragment2 is shown
        assertEquals(rootFragment2, fragmentManager.attachedFragments()[0])
        // But no scroll command has been received
        scrollFragmentListener.assertEmpty()
        // When we click on tab2 a third time
        clickTab(tab2)
        // Then rootFFragment2 is still shown
        assertEquals(rootFragment2, fragmentManager.attachedFragments()[0])
        // And scroll command was received for rootFragment2
        scrollFragmentListener.assertValue(rootFragment2)
        // When we switch back to tab1
        clickTab(tab1)
        // Then frag1_2 is still the top fragment
        assertEquals(frag1_2, fragmentManager.attachedFragments()[0])
    }

    @Test
    fun switchTab() {
        // Given an BottomNavigator initialized with tab2
        val bottomNavView = generateBottomViewMock()
        val bottomNavigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // Then bottomNavView is set to tab2
        verify(bottomNavView).selectedItemId = tab2
        // And rootFragment2 is shown
        assertTrue(fragmentManager.attachedFragments().contains(rootFragment2))
        // When we click to tab3
        bottomNavigator.switchTab(tab3)
        // Then rootFragment3 is shown
        assertTrue(fragmentManager.attachedFragments().contains(rootFragment3))
        assertTrue(fragmentManager.detachedFragments().contains(rootFragment2))
    }

    @Test
    fun addFragment() {
        // Given a BottomNavigator initialized with tab2
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = generateBottomViewMock(),
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // When we add a fragment
        val fragment2_1 = mock<Fragment>()
        navigator.addFragment(fragment2_1)
        // Then fragment2_1 is shown
        assertTrue(fragmentManager.attachedFragments().contains(fragment2_1))
        // And rootFragment2 is detached
        assertTrue(fragmentManager.detachedFragments().contains(rootFragment2))
    }

    @Test
    fun addFragmentToDifferentTab() {
        // Given a BottomNavigator initialized with tab2
        val bottomNavView = generateBottomViewMock()
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // When we add a fragment to tab1
        val fragment1_2 = mock<Fragment>()
        navigator.addFragment(fragment1_2, tab1)
        // Then fragment1_2 is shown
        assertTrue(fragmentManager.attachedFragments().contains(fragment1_2))
        // And tab1 is selected
        verify(bottomNavView).selectedItemId = tab1
        // And rootFragment2 and rootFragment1 is detached
        assertEquals(
            setOf(rootFragment1, rootFragment2),
            HashSet(fragmentManager.detachedFragments())
        )
    }

    @Test
    fun pop() {
        // Given a navigator with two fragments on tab2
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = generateBottomViewMock(),
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        navigator.addFragment(mock())
        // When we pop
        navigator.pop()
        // Then rootFragment2 is shown
        assertEquals(rootFragment2, fragmentManager.attachedFragments()[0])
        // When we pop again Then pop returns false
        assertFalse(navigator.pop())
    }

    @Test
    fun popAcrossTabs() {
        // Given a navigator with two fragments on tab2
        val bottomNavView = generateBottomViewMock()
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        val frag2_2 = mock<Fragment>()
        navigator.addFragment(frag2_2)
        verify(bottomNavView).selectedItemId = tab2
        // And two fragments on tab1
        val frag1_2 = mock<Fragment>()
        navigator.addFragment(frag1_2, tab1)
        verify(bottomNavView).selectedItemId = tab1
        // When we pop 2 times
        navigator.pop()
        navigator.pop()
        // Then we we are back on tab2
        verify(bottomNavView, times(2)).selectedItemId = tab2
        // And frag2_2 is showing
        assertEquals(frag2_2, fragmentManager.attachedFragments()[0])
    }

    @Test
    fun popAfterSwitchingTabs() {
        // Given a navigator on tab2
        val bottomNavView = generateBottomViewMock()
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // When we switch to tab3
        navigator.switchTab(3)
        // And we switch to tab 4
        navigator.switchTab(4)
        // And we add a fragment
        val frag4_2 = mock<Fragment>()
        navigator.addFragment(frag4_2)
        // And we switch to tab3
        navigator.switchTab(3)
        // Then the pop order will be tab4 two times and then tab1
        navigator.pop()
        assertEquals(frag4_2, fragmentManager.attachedFragments()[0])
        navigator.pop()
        assertEquals(rootFragment4, fragmentManager.attachedFragments()[0])
        navigator.pop()
        assertEquals(rootFragment2, fragmentManager.attachedFragments()[0])
        val inorder = inOrder(bottomNavView)
        inorder.verify(bottomNavView).selectedItemId = 2
        inorder.verify(bottomNavView).selectedItemId = 3
        inorder.verify(bottomNavView).selectedItemId = 4
        inorder.verify(bottomNavView).selectedItemId = 3
        inorder.verify(bottomNavView).selectedItemId = 4
        inorder.verify(bottomNavView).selectedItemId = 2
    }

    @Test
    fun testCreateWithDetachability() {
        // Given Some root fragments are detachable and some are not
        val bottomNavView = generateBottomViewMock()
        val rootFragment1 = mock<Fragment>()
        val rootFragment2 = mock<Fragment>()
        val rootFragment3 = mock<Fragment>()
        val rootFragment4 = mock<Fragment>()
        val rootFragmentsFactory = mapOf(
            Pair(tab1, { FragmentInfo(rootFragment1, true) }),
            Pair(tab2, { FragmentInfo(rootFragment2, false) }),
            Pair(tab3, { FragmentInfo(rootFragment3, true) }),
            Pair(tab4, { FragmentInfo(rootFragment4, false) })
        )
        // And we start in tab2
        val navigator = BottomNavigator.onCreateWithDetachability(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // When we switch to tab1
        navigator.switchTab(tab1)
        // Then rootFragment2 is hidden not detached
        assertTrue(fragmentManager.hiddenFragments().contains(rootFragment2))
        // When we switch to tab2
        navigator.switchTab(tab2)
        // Then rootFragment1 is detached
        assertTrue(fragmentManager.detachedFragments().contains(rootFragment1))
    }

    @Test
    fun testAddWithDetachability() {
        // Given Some root fragments are detachable and some are not
        val bottomNavView = generateBottomViewMock()
        val rootFragmentsFactory = mapOf(
            Pair(tab1, { FragmentInfo(mock(), true) }),
            Pair(tab2, { FragmentInfo(mock(), false) }),
            Pair(tab3, { FragmentInfo(mock(), true) }),
            Pair(tab4, { FragmentInfo(mock(), false) })
        )
        val navigator = BottomNavigator.onCreateWithDetachability(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // When we add a non-detatchable fragment
        val frag2_2 = mock<Fragment>()
        navigator.addFragment(frag2_2, false)
        // And we switch tabs
        navigator.switchTab(tab3)
        // Then frag2_2 is hidden not detached
        assertTrue(fragmentManager.hiddenFragments().contains(frag2_2))
        assertFalse(fragmentManager.detachedFragments().contains(frag2_2))
        // When we add a detachable fragment
        val frag3_2 = mock<Fragment>()
        navigator.addFragment(frag3_2, true)
        // And we switch tabs
        navigator.switchTab(tab4)
        // Then frag3_2 is detached not hidden
        assertTrue(fragmentManager.detachedFragments().contains(frag3_2))
        assertFalse(fragmentManager.hiddenFragments().contains(frag3_2))
    }

    @Test
    fun testAddTemporaryRootFragment_noExistingFragment() {
        // Given a navigator initialized on tab2
        val bottomNavView = generateBottomViewMock()
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // When we add a temporary root on tab3
        val fragment: Fragment = mock()
        navigator.addRootFragment(tab3, fragment)
        // Then bottomNavView is switched from tab2 to tab3
        val inorder = inOrder(bottomNavView)
        inorder.verify(bottomNavView).selectedItemId = 2
        inorder.verify(bottomNavView).selectedItemId = 3
        // And the new fragment is shown
        assertTrue(fragmentManager.attachedFragments().contains(fragment))
    }

    @Test
    fun testAddTemporaryRootFragment_existingFragment() {
        // Given a navigator initialized on tab2
        val bottomNavView = generateBottomViewMock()
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // And an existing fragment on tab3
        val frag3_2 = mock<Fragment>()
        navigator.addFragment(frag3_2, tab3)
        navigator.switchTab(tab2)
        // When we add a temporary root on tab3
        val fragment: Fragment = mock()
        navigator.addRootFragment(tab3, fragment)
        // Then bottomNavView is switched from tab2 to tab3
        val inorder = inOrder(bottomNavView)
        inorder.verify(bottomNavView).selectedItemId = 2
        inorder.verify(bottomNavView).selectedItemId = 3
        inorder.verify(bottomNavView).selectedItemId = 2
        inorder.verify(bottomNavView).selectedItemId = 3
        // And the new fragment is in the fragmentManager
        assertEquals(fragment, fragmentManager.attachedFragments()[0])
        // And the old fragments on tab3 are gone
        assertNull(fragmentManager.map.values.find { it.fragment == frag3_2 })
    }

    @Test
    fun testResetDontResetRootFragment() {
        // Given a navigator initialized on tab2
        val bottomNavView = generateBottomViewMock()
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        // And 3 more fragments are added
        val frag2_2 = mock<Fragment>()
        val frag2_3 = mock<Fragment>()
        val frag2_4 = mock<Fragment>()
        navigator.addFragment(frag2_2)
        navigator.addFragment(frag2_3)
        navigator.addFragment(frag2_4)
        // When we reset the tab without resetting the root
        navigator.reset(tab2, false)
        // Then the root fragment is still the original root fragment
        assertEquals(rootFragment2, navigator.currentFragment())
    }

    @Test
    fun testResetYesResetRootFragment() {
        // Given a navigator initialized on tab2
        val bottomNavView = generateBottomViewMock()
        val rootFragmentsFactory = mapOf(
            Pair(tab1, { mock<Fragment>() }),
            Pair(tab2, { mock<Fragment>() }),
            Pair(tab3, { mock<Fragment>() }),
            Pair(tab4, { mock<Fragment>() })
        )
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = bottomNavView,
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        val rootFragment2 = navigator.currentFragment()
        // And 3 more fragments are added
        navigator.addFragment(mock())
        navigator.addFragment(mock())
        navigator.addFragment(mock())
        // When we reset the tab without resetting the root
        navigator.reset(tab2, true)
        // Then the root fragment is still the original root fragment
        assertTrue(rootFragment2 != navigator.currentFragment())
    }

    @Test fun clear() {
        // Given a BottomNavigator with two fragments on tab2
        val bottomNavigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = generateBottomViewMock(),
            defaultTab = tab2
        ).apply {
            // And the activity starts
            activityDelegate!!.onActivityStart()
        }
        val frag2_2 = mock<Fragment> { on { toString() } doReturn "fragment2_2" }
        bottomNavigator.addFragment(frag2_2)
        // And two fragments on tab1
        bottomNavigator.switchTab(tab1)
        val frag1_2 = mock<Fragment> { on { toString() } doReturn "fragment1_2" }
        bottomNavigator.addFragment(frag1_2)
        // And we're on the default tab
        bottomNavigator.switchTab(tab2)
        // When we call clearAll()
        bottomNavigator.clearAll()
        // Then there is only one fragment in fragmentManager
        assertEquals(1, fragmentManager.map.size)
        // And the defaultTab's fragment is shown
        assertTrue { fragmentManager.attachedFragments()[0] == rootFragment2 }

    }

    @Test fun `all fragment transactions produce an infoStream event`() {
        // Given a BottomNavigator
        val navigator = BottomNavigator.onCreate(
            activity = generateActivityMock(),
            rootFragmentsFactory = rootFragmentsFactory,
            fragmentContainer = 123,
            bottomNavigationView = generateBottomViewMock(),
            defaultTab = tab2
        )
        val infoSubscriber = navigator.infoStream().test()
        // When the activity starts
        navigator.activityDelegate!!.onActivityStart()
        // Then we get an NewFragmentAdded info event
        infoSubscriber.assertLatest {
            it is NavigatorAction.NewFragmentAdded
                && it.fragment == rootFragment2
        }

        // When we add a fragment
        val frag2_2 = mock<Fragment>()
        navigator.addFragment(frag2_2, false)
        // Then we get an AddAndShow info event
        infoSubscriber.assertLatest {
            it is NavigatorAction.NewFragmentAdded
                && it.fragment == frag2_2
        }

        // When we switch to tab1
        navigator.switchTab(tab1)
        // Then we get a tab switched event
        infoSubscriber.assertNextToLast {
            it is NavigatorAction.TabSwitched && it.newTab == tab1 && it.previousTab == tab2
        }
        // And a fragment added event
        infoSubscriber.assertLatest {
            it is NavigatorAction.NewFragmentAdded && it.fragment == rootFragment1
        }

        // When we add a root fragment to tab 1
        val frag1root = mock<Fragment>()
        navigator.addRootFragment(tab1, frag1root)
        // Then we FragmentRemoved event because tab1's default root fragment is being replaced
        infoSubscriber.assertNextToLast { it is NavigatorAction.FragmentRemoved }
        // And we get NewFragmentAdded with the new root fragment
        infoSubscriber.assertLatest {
            it is NavigatorAction.NewFragmentAdded && it.fragment == frag1root
        }

        // When we add a fragment to a different tab
        val frag3_2 = mock<Fragment>()
        navigator.addFragment(frag3_2, tab3)
        // Then we get an TabSwitched event
        infoSubscriber.assertValueAt(infoSubscriber.valueCount() - 3) {
            it is NavigatorAction.TabSwitched && it.newTab == tab3 && it.previousTab == tab1
        }
        // And a NewFragmentAdded event with the tab3 root fragment
        infoSubscriber.assertNextToLast {
            it is NavigatorAction.NewFragmentAdded && it.fragment == rootFragment3
        }
        // And the NewFragmentAdded event with the new fragment
        infoSubscriber.assertLatest {
            it is NavigatorAction.NewFragmentAdded && it.fragment == frag3_2
        }

        // When we pop and it stays on the same tab
        navigator.pop()
        // Then we get a fragment removed event
        infoSubscriber.assertLatest { it is NavigatorAction.FragmentRemoved }
        // When we pop across tabs
        navigator.pop()
        // Then we get the tab switched event
        infoSubscriber.assertNextToLast {
            it is NavigatorAction.TabSwitched && it.newTab == tab1 && it.previousTab == tab3
        }
        // And a fragment removed event
        infoSubscriber.assertLatest { it is NavigatorAction.FragmentRemoved }
    }

    private fun <T> TestObserver<T>.assertLatest(predicate: (t: T) -> Boolean) {
        assertValueAt(valueCount() - 1, predicate)
    }

    private fun <T> TestObserver<T>.assertNextToLast(predicate: (t: T) -> Boolean) {
        assertValueAt(valueCount() - 2, predicate)
    }

    private fun generateActivityMock(): FragmentActivity {
        val activity = mock<FragmentActivity>()
        whenever(activity.application).doReturn(mock())
        whenever(activity.getViewModelStore()).doReturn(vmStore)
        whenever(activity.lifecycle).doReturn(mock())
        whenever(activity.supportFragmentManager).doReturn(fragmentManager)
        return activity
    }

    private var selectListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    private fun clickTab(tab: Int) {
        val menuItem = mock<MenuItem>()
        whenever(menuItem.getItemId()).thenReturn(tab)
        selectListener?.onNavigationItemSelected(menuItem)
    }

    private fun generateBottomViewMock(): BottomNavigationView {
        val menuItem1 = mock<MenuItem> { on { itemId } doReturn tab1 }
        val menuItem2 = mock<MenuItem> { on { itemId } doReturn tab2 }
        val menuItem3 = mock<MenuItem> { on { itemId } doReturn tab3 }
        val menuItem4 = mock<MenuItem> { on { itemId } doReturn tab4 }
        val mockBottomNavMenu = mock<Menu> {
            on { size() } doReturn 4
            on { getItem(0) } doReturn menuItem1
            on { getItem(1) } doReturn menuItem2
            on { getItem(2) } doReturn menuItem3
            on { getItem(3) } doReturn menuItem4
        }
        val bottomNav = mock<BottomNavigationView> { on { menu } doReturn mockBottomNavMenu }

        doAnswer {
            selectListener =
                it.arguments[0] as BottomNavigationView.OnNavigationItemSelectedListener
        }
            .whenever(bottomNav).setOnNavigationItemSelectedListener(any())

        whenever(bottomNav.setSelectedItemId(any())).thenAnswer {
            clickTab(it.arguments[0] as Int)
        }

        return bottomNav
    }
}