package com.gmail.ortuchr.finance.presentation.base

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

abstract class BaseRouter<A: BaseActivity>(val activity: A) {

    fun goBack() {
        hideKeyboard()
        activity.onBackPressed()
    }

    fun showError(e: Throwable) {
        Toast.makeText(activity,
            "Error: $e",
            Toast.LENGTH_SHORT).show()
    }

    fun replaceFragment(fragmentManager: androidx.fragment.app.FragmentManager,
                        fragment: BaseFragment,
                        containerResId: Int,
                        addToBackStack: Boolean = true) {

        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(containerResId,
            fragment,
            fragment::class.java.canonicalName)
        if (addToBackStack) fragmentTransition.addToBackStack(null)
        fragmentTransition.commit()
    }

    private fun hideKeyboard() {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        var view = activity.currentFocus
        if (view == null)
            view = View(activity)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}