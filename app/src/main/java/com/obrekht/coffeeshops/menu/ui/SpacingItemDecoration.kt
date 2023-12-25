package com.obrekht.coffeeshops.menu.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SpacingItemDecoration(
    private val size: Int,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount

            val position = parent.getChildAdapterPosition(view)
            val column: Int = position % spanCount

            if (includeEdge) {
                outRect.left = size - column * size / spanCount
                outRect.right = (column + 1) * size / spanCount
                if (position < spanCount) {
                    outRect.top = size
                }
                outRect.bottom = size
            } else {
                outRect.left = column * size / spanCount
                outRect.right = size - (column + 1) * size / spanCount
                if (position >= spanCount) {
                    outRect.top = size
                }
            }
        }
    }
}
