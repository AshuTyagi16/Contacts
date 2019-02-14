package com.pratilipi.contacts.ui.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ItemDecorator(
    private val spaceLeft: Int,
    private val spaceRight: Int,
    private val spaceTop: Int,
    private val spaceBottom: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = spaceLeft
        outRect.right = spaceRight
        outRect.bottom = spaceBottom

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = 10
        } else {
            outRect.top = spaceTop
        }
    }
}