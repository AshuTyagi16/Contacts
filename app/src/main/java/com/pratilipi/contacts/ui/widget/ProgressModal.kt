package com.pratilipi.contacts.ui.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.pratilipi.contacts.R
import kotlinx.android.synthetic.main.layout_progress_modal.*

class ProgressModal(context: Context, val message: String? = null) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_progress_modal)
        progressBar.indeterminateDrawable = DoubleBounce()
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
        window?.setDimAmount(0.75f)
        setCanceledOnTouchOutside(false)
        message?.let {
            tvMessage.text = it
        }
    }
}