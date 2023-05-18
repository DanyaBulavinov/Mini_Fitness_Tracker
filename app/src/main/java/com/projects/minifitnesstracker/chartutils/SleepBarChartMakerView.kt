package com.projects.minifitnesstracker.chartutils

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.projects.minifitnesstracker.R

class SleepBarChartMakerView (context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.marker_text)

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight?) {
        val hours = (e.y / 60).toInt()
        val minutes = (e.y % 60).toInt()
        val text = "$hours:$minutes"
        textView.text = text

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