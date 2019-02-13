package com.pratilipi.contacts.ui.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.pratilipi.contacts.R
import kotlinx.android.synthetic.main.layout_permission_modal.*

class PermissionModal(context: Context) : Dialog(context) {

    private lateinit var onAskPermissionClickListener: OnAskPermissionClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_permission_modal)
        window?.setDimAmount(0.95f)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        parent.setOnClickListener {
            if (::onAskPermissionClickListener.isInitialized)
                onAskPermissionClickListener.onAskPermissionClick()
        }
    }

    interface OnAskPermissionClickListener {
        fun onAskPermissionClick()
    }

    fun setOnAskPermissionClickListener(onAskPermissionClickListener: OnAskPermissionClickListener) {
        this.onAskPermissionClickListener = onAskPermissionClickListener
    }
}