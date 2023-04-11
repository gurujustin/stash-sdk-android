/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.core.exceptions.base.BasePaymentException
import com.mobilabsolutions.stash.core.px
import kotlinx.android.synthetic.main.snackbar_layout.view.*

object SnackBarExtensions {

    operator fun invoke(body: SnackBarExtensions.() -> Unit): Unit = body.invoke(this)

    private val TOP_MARGIN = 0.px
    private val WIDTH = 64.px

    fun Throwable.getErrorSnackBar(view: View, stashUIConfiguration: StashUiConfiguration? = null): Snackbar {
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
        val snackBarView = snackBar.view as Snackbar.SnackbarLayout
        snackBarView.setPadding(0, 0, 0, 0)
        val snackView = LayoutInflater.from(view.context).inflate(R.layout.snackbar_layout, snackBarView, false)
        val backgroundColor: Int = stashUIConfiguration?.snackBarBackground ?: R.color.carnation
        snackView.setBackgroundColor(ContextCompat.getColor(view.context, backgroundColor))

        if (this is BasePaymentException && this.errorTitle != null) {
            snackView.snackbar_title.text = this.errorTitle
        } else {
            // TODO: Biju: Find a better way
            snackView.snackbar_title.text = this.javaClass.simpleName.replace("Exception", " Error")
        }
        snackView.snackbar_text.text = this.message
        snackView.close.setOnClickListener {
            snackBar.view.visibility = View.GONE // We do this to prevent dismissal animation
            snackBar.dismiss()
        }

        val params = snackBar.view.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            with(params) {
                gravity = Gravity.TOP
                height = WIDTH
                width = CoordinatorLayout.LayoutParams.MATCH_PARENT
                setMargins(0, TOP_MARGIN, 0, 0)
            }
        } else {
            with(params as FrameLayout.LayoutParams) {
                gravity = Gravity.TOP
                height = WIDTH
                width = FrameLayout.LayoutParams.MATCH_PARENT
                setMargins(0, TOP_MARGIN, 0, 0)
            }
        }
        snackBar.view.layoutParams = params
        snackBar.view.elevation = 0f

        snackBarView.addView(snackView, 0)
        // We don't want to show snackBar showing animation, because it looks broken, since our
        // snackbar is on top
        snackBar.view.visibility = View.INVISIBLE
        snackBar.addCallback(
                object : Snackbar.Callback() {
                    override fun onShown(snackbar: Snackbar?) {
                        super.onShown(snackbar)
                        snackbar!!.view.visibility = View.VISIBLE
                    }
                }
        )

        return snackBar
    }

    fun Snackbar.dismissWithoutAnimating() {
        view.visibility = View.INVISIBLE
        dismiss()
    }
}