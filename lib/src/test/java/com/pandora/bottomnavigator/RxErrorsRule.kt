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

import io.reactivex.plugins.RxJavaPlugins
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.LinkedBlockingDeque

/**
 * JUnit rule to throw uncaught RxJava errors.
 */
class RxErrorsRule : TestWatcher() {
    private val errors = LinkedBlockingDeque<Throwable>()

    override fun starting(description: Description?) {
        RxJavaPlugins.setErrorHandler { t -> errors.add(t) }
    }

    override fun finished(description: Description?) {
        RxJavaPlugins.setErrorHandler(null)
        errors.forEach { throw it }
        errors.clear()
    }
}