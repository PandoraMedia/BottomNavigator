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