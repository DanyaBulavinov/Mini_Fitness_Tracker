package com.projects.minifitnesstracker.chartutils

import android.content.Context
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet.Layout
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.projects.minifitnesstracker.R
import com.projects.minifitnesstracker.databinding.CustomMarkerLayoutBinding


class BarChartMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.marker_text)

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight?) {
        textView.text = e.y.toString()

        // this will perform necessary layouting
        super.refreshContent(e, highlight)
    }

    private var mOffset: MPPointF? = null
    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return mOffset!!
    }
}