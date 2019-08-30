package com.pandora.bottomnavigator

import android.os.Parcelable

/**
 * MultipleStacks represents a map of tabs to fragment stacks.
 * Different implementations will behave differently as fragments are pop'd.
 */
interface MultipleStacks<TAB, FRAGMENT : Parcelable> {

    /**
     * Removes the current top fragment and returns it.
     * Returns null when there are no more fragments.
     */
    fun pop(): FRAGMENT?

    /**
     * Add a fragment to the specified tab and switches to that tab.
     */
    fun push(tab: TAB, value: FRAGMENT)

    /**
     * Switch to the tab's stack so the top fragment in that stack can be shown
     */
    fun switchToTab(tab: TAB)

    /**
     * Removes the tab and its corresponding stack.
     */
    fun remove(tab: TAB)

    /**
     * Get the stack for the given tab.
     */
    operator fun get(tab: TAB): List<FRAGMENT>?

    fun keys(): Set<TAB>

    /**
     * Whether the tab has a non-empty stack
     */
    fun stackExists(tab: TAB): Boolean

    /**
     * return the current tab and the top fragment in the tab's stack.
     */
    fun peek(): Pair<TAB, FRAGMENT>?

    /**
     * return the current tab
     */
    fun peekKey(): TAB?

    /**
     * return the fragment at the top of the stack of the current tab
     */
    fun peekValue(): FRAGMENT?

    /**
     * removes all tabs and their fragments.
     */
    fun clear()
}