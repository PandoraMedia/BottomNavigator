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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class StackOfStacksTest {
    @Test
    fun stackExists() {
        // given an empty stack
        val stack = StackOfStacks<Int, Int>()
        // then stack 1 does not exist
        assertEquals(false, stack.stackExists(1))
        // When we add a stack for key 1
        stack.push(1, 1)
        // Then that stack does exist
        assertEquals(true, stack.stackExists(1))
        // When we pop the stack
        stack.pop()
        // Then that stack doesn't exist anymore
        assertEquals(false, stack.stackExists(1))
    }

    @Test
    fun push() {
        // Given an empty stack
        val stack = StackOfStacks<Int, Int>()
        // Then stack 1 doesn't exist.
        assertEquals(false, stack.stackExists(1))
        // When we push 1 onto stack 1
        stack.push(1, 1)
        // Then stack 1 exists.
        assertEquals(true, stack.stackExists(1))
        // When we push another value onto stack 1
        stack.push(1, 2)
        // Then stack 1 has the right values in the right order.
        assertEquals(listOf(1, 2), stack.get(1))
        // When we push onto stack 2
        stack.push(2, 1)
        // Then stack 2 is at the top of the stacks.
        assertEquals(2, stack.peekKey())
        // When we push onto stack 1
        stack.push(1, 3)
        // Then stack 1 is now at the top of the stacks
        assertEquals(1, stack.peekKey())
        // And stack 1 has all the elements that were pushed into it.
        assertEquals(listOf(1, 2, 3), stack.get(1))
    }

    @Test
    fun moveToTop() {
        // Given we push onto stack 1 and stack 2
        val stack = StackOfStacks<Int, Int>()
        stack.push(1, 1)
        stack.push(2, 1)
        // Then stack 2 is at the top of the stacks.
        assertEquals(2, stack.peekKey())
        // When we move stack 1 to the top.
        stack.moveToTop(1)
        // Then stack 1 is at the top.
        assertEquals(1, stack.peekKey())
        // When we move a non existing stack to the top
        stack.moveToTop(3)
        // Then stack 1 is still at the top
        assertEquals(1, stack.peekKey())
    }

    @Test
    fun pop() {
        // Given an empty stack
        val stack = StackOfStacks<Int, Int>()
        // When we pop Then we get a null value
        assertEquals(null, stack.pop())
        // Given stack 1 with a stack of 1, 2
        stack.push(1, 1)
        stack.push(1, 2)
        // When we pop Then we get 2 back
        assertEquals(2, stack.pop())
        // When we pop again Then we get 1 back
        assertEquals(1, stack.pop())
        // And the stack is gone.
        assertFalse(stack.stackExists(1))
        // Given 2 stacks each with a 1 sized stack
        stack.push(1, 1)
        stack.push(2, 1)
        // When we pop
        stack.pop()
        // Then the top of the stack is the first stack
        assertEquals(1, stack.peekKey())
    }

    @Test
    fun remove() {
        // Given a stack with 3 stacks
        val stack = StackOfStacks<Int, Int>()
        stack.push(1, 1)
        stack.push(2, 1)
        stack.push(3, 1)
        // When we remove the middle stack
        stack.remove(2)
        // Then the stack doesn't exist
        assertFalse(stack.stackExists(2))
    }

    @Test
    fun get() {
        // Given a stack with 3 stacks
        val stack = StackOfStacks<Int, Int>()
        stack.push(1, 1)
        stack.push(2, 1)
        // And stack 3 has 3 elements in it
        stack.push(3, 1)
        stack.push(3, 2)
        stack.push(3, 3)
        // When we get stack 3 Then we see the 3 elements in order
        assertEquals(listOf(1, 2, 3), stack.get(3))
    }

    @Test
    fun peek() {
        val stack = StackOfStacks<Int, Int>()
        assertNull(stack.peek())
        stack.push(1, 1)
        assertEquals(Pair(1, 1), stack.peek())
        stack.push(1, 2)
        assertEquals(Pair(1, 2), stack.peek())
        stack.push(2, 1)
        assertEquals(Pair(2, 1), stack.peek())
        stack.pop()
        assertEquals(Pair(1, 2), stack.peek())
    }

    @Test
    fun peekKey() {
        val stack = StackOfStacks<Int, Int>()
        assertNull(stack.peekKey())
        stack.push(1, 1)
        assertEquals(1, stack.peekKey())
        stack.push(1, 2)
        assertEquals(1, stack.peekKey())
        stack.push(2, 1)
        assertEquals(2, stack.peekKey())
        stack.pop()
        assertEquals(1, stack.peekKey())
    }

    @Test
    fun peekValue() {
        val stack = StackOfStacks<Int, Int>()
        assertNull(stack.peekValue())
        stack.push(1, 1)
        assertEquals(1, stack.peekValue())
        stack.push(1, 2)
        assertEquals(2, stack.peekValue())
        stack.push(2, 1)
        assertEquals(1, stack.peekValue())
        stack.pop()
        assertEquals(2, stack.peekValue())
    }

    @Test
    fun clear() {
        val stack = StackOfStacks<Int, Int>()
        stack.push(1, 1)
        stack.push(1, 2)
        stack.push(2, 3)
        stack.push(2, 4)
        stack.push(2, 5)
        stack.clear()
        assertFalse(stack.stackExists(1))
        assertFalse(stack.stackExists(2))
    }
}