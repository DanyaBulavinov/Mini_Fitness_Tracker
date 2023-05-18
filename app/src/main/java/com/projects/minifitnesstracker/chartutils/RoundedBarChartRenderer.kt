package com.projects.minifitnesstracker.chartutils

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler


class RoundedBarChart(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) :
    BarChartRenderer(chart, animator, viewPortHandler) {
    private var mRadius = 5f
    fun setmRadius(mRadius: Float) {
        this.mRadius = mRadius
    }

    override fun drawHighlighted(c: Canvas?, indices: Array<out Highlight>?) {
        super.drawHighlighted(c, indices)
        val barData = mChart.barData

        for (high in indices!!) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)
            if (set == null || !set.isHighlightEnabled) {
                continue
            }
            val e = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set)) {
                continue
            }
            val trans = mChart.getTransformer(set.axisDependency)
            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.alpha = set.highLightAlpha
            val isStack = high.stackIndex >= 0 && e.isStacked
            val y1: Float
            val y2: Float
            if (isStack) {
                if (mChart.isHighlightFullBarEnabled) {
                    y1 = e.positiveSum
                    y2 = -e.negativeSum
                } else {
                    val range = e.ranges[high.stackIndex]
                    y1 = range.from
                    y2 = range.to
                }
            } else {
                y1 = e.y
                y2 = 0f
            }
            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)
            setHighlightDrawPos(high, mBarRect)
            val path2: Path = roundRect(
                RectF(
                    mBarRect.left, mBarRect.top, mBarRect.right,
                    mBarRect.bottom
                ), mRadius, mRadius, true, true, true, true
            )
            c!!.drawPath(path2, mHighlightPaint)
        }
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)
        mShadowPaint.color = dataSet.barShadowColor
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY
        if (mBarBuffers != null) {
            // initialize the buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.feed(dataSet)
            trans.pointValuesToPixel(buffer.buffer)

            // if multiple colors
            if (dataSet.colors.size > 1) {
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        j += 4
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                    if (mChart.isDrawBarShadowEnabled) {
                        if (mRadius > 0) c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2], mViewPortHandler.contentBottom()
                            ), mRadius, mRadius, mShadowPaint
                        ) else c.drawRect(
                            buffer.buffer[j], mViewPortHandler.contentTop(),
                            buffer.buffer[j + 2], mViewPortHandler.contentBottom(), mShadowPaint
                        )
                    }

                    // Set the color for the currently drawn value. If the index
                    // is
                    // out of bounds, reuse colors.
                    mRenderPaint.color = dataSet.getColor(j / 4)
                    if (mRadius > 0) {
                        val path: Path = roundRect(
                            RectF(
                                buffer.buffer[j],
                                buffer.buffer[j + 1],
                                buffer.buffer[j + 2],
                                buffer.buffer[j + 3]
                            ),
                            mRadius, mRadius, true, true, true, true
                        )
                        c.drawPath(path, mRenderPaint)
                    } else c.drawRect(
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        buffer.buffer[j + 2], buffer.buffer[j + 3], mRenderPaint
                    )
                    j += 4
                }
            } else {
                mRenderPaint.color = dataSet.color
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        j += 4
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                    if (mChart.isDrawBarShadowEnabled) {
                        if (mRadius > 0) c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()
                            ), mRadius, mRadius, mShadowPaint
                        ) else c.drawRect(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint
                        )
                    }
                    if (mRadius > 0) {
                        val path: Path = roundRect(
                            RectF(
                                buffer.buffer[j],
                                buffer.buffer[j + 1],
                                buffer.buffer[j + 2],
                                buffer.buffer[j + 3]
                            ),
                            mRadius, mRadius, true, true, true, true
                        )
                        c.drawPath(path, mRenderPaint)
                    } else c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint
                    )
                    j += 4
                }
            }
        }
    }

    companion object {
        private fun roundRect(
            rect: RectF,
            rx: Float,
            ry: Float,
            tl: Boolean,
            tr: Boolean,
            br: Boolean,
            bl: Boolean
        ): Path {
            var rx = rx
            var ry = ry
            val top = rect.top
            val left = rect.left
            val right = rect.right
            val bottom = rect.bottom
            val path = Path()
            if (rx < 0) {
                rx = 0f
            }
            if (ry < 0) {
                ry = 0f
            }
            val width = right - left
            val height = bottom - top
            if (rx > width / 2) {
                rx = width / 2
            }
            if (ry > height / 2) {
                ry = height / 2
            }
            val widthMinusCorners = width - 2 * rx
            val heightMinusCorners = height - 2 * ry
            path.moveTo(right, top + ry)
            if (tr) {
                //top-right corner
                path.rQuadTo(0f, -ry, -rx, -ry)
            } else {
                path.rLineTo(0f, -ry)
                path.rLineTo(-rx, 0f)
            }
            path.rLineTo(-widthMinusCorners, 0f)
            if (tl) {
                //top-left corner
                path.rQuadTo(-rx, 0f, -rx, ry)
            } else {
                path.rLineTo(-rx, 0f)
                path.rLineTo(0f, ry)
            }
            path.rLineTo(0f, heightMinusCorners)
            if (bl) {
                //bottom-left corner
                path.rQuadTo(0f, ry, rx, ry)
            } else {
                path.rLineTo(0f, ry)
                path.rLineTo(rx, 0f)
            }
            path.rLineTo(widthMinusCorners, 0f)
            if (br) {
                //bottom-right corner
                path.rQuadTo(rx, 0f, rx, -ry)
            } else {
                path.rLineTo(rx, 0f)
                path.rLineTo(0f, -ry)
            }
            path.rLineTo(0f, -heightMinusCorners)
            path.close() //Given close, last lineto can be removed.
            return path
        }
    }
}