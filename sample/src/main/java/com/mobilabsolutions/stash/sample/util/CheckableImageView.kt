package com.mobilabsolutions.stash.sample.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView

class CheckableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs), Checkable {

    private var isChecked = false

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED)
        }
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean = isChecked

    override fun toggle() {
        isChecked = !isChecked
    }

    companion object {
        private val CHECKED = intArrayOf(android.R.attr.state_checked)
    }
}
