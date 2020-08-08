package com.xently.tests.ui

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource
import java.util.*

/**
 * Used to make Espresso work with DataBinding. Without it the tests will be flaky because
 * DataBinding uses Choreographer class to synchronize its view updates hence using this to
 * monitor a launched fragment in fragment scenario will make Espresso wait before doing
 * additional checks
 */
class DataBindingIdlingResource : IdlingResource {
    // Holds whether isIdle was called and the result was false. We track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never thought we were idle in the first place.
    private var wasNotIdle = false
    private var activity: FragmentActivity? = null
    override fun getName(): String = "DataBinding $id"

    override fun isIdleNow(): Boolean {
        var idle = false
        for (b in bindings) {
            if (!b!!.hasPendingBindings()) {
                idle = true
                break
            }
        }
        if (idle) {
            if (wasNotIdle) {
                // Notify observers to avoid Espresso race detector.
                for (cb in idlingCallbacks) cb.onTransitionToIdle()
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true
            // Check next frame.
            if (activity != null) {
                activity!!.findViewById<View>(R.id.content)
                    .postDelayed({ this.isIdleNow }, 16)
            }
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallbacks += callback
    }

    /**
     * Sets the activity from an [ActivityScenario] to be used from [DataBindingIdlingResource].
     */
    fun <T : FragmentActivity> monitorActivity(activityScenario: ActivityScenario<T>) {
        activityScenario.onActivity { activity: T -> this.monitorActivity(activity) }
    }

    /**
     * Sets the fragment from a [FragmentScenario] to be used from [DataBindingIdlingResource].
     */
    fun <T : Fragment> monitorFragment(fragmentScenario: FragmentScenario<T>) {
        fragmentScenario.onFragment { fragment: T -> this.monitorFragment(fragment) }
    }

    fun <T : FragmentActivity> monitorActivity(activity: T) {
        this.activity = activity
    }

    fun <T : Fragment> monitorFragment(fragment: T) {
        activity = fragment.requireActivity()
    }

    private fun getBinding(view: View?): ViewDataBinding? = DataBindingUtil.getBinding(view!!)

    private val bindings: List<ViewDataBinding?>
        get() {
            val fragments =
                if (activity == null) emptyList() else activity!!.supportFragmentManager
                    .fragments
            val bindings: MutableList<ViewDataBinding?> = ArrayList()
            for (f in fragments) {
                if (f.view == null) continue
                bindings.add(getBinding(f.view))
                for (cf in f.childFragmentManager.fragments) {
                    if (cf.view == null) continue
                    bindings.add(getBinding(cf.view))
                }
            }
            return bindings
        }

    companion object {
        // Give it a unique id to work around an Espresso bug where you cannot register/unregister
        // an idling resource with the same name.
        private val id = UUID.randomUUID().toString()

        // List of registered callbacks
        private val idlingCallbacks: MutableList<IdlingResource.ResourceCallback> = ArrayList()
    }
}