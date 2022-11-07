package com.example.testproject

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import java.lang.ref.WeakReference

/**
 * TODO: document your custom view class.
 */
class SearchHistoryTextViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(
    context,
    attrs,
    defStyleAttr
) {
    var rightMarginWidthForClearSpace = 0

    init {
        rightMarginWidthForClearSpace =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics)
                .toInt()
    }

    val childViews: MutableList<WeakReference<TextView>> = mutableListOf()
    var collapseImageView: ImageView? = null
    var measuredLineCount: Int = 1
    var mOriginMeasuredParentHeight: Int = 0
    var textStrings: MutableList<String> = mutableListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            removeAllViews()
            for ((index, s) in textStrings.withIndex()) {
                val childTextView: TextView =
                    if (childViews.size in 1 until index) {
                        val get = childViews[index].get()
                        if (get == null) {
                            val textView = TextView(context)
                            childViews.add(WeakReference(textView))
                            textView
                        } else get
                    } else {
                        if (childCount in 1 until index) {
                            val textView = getChildAt(index) as TextView
                            childViews.add(WeakReference(textView))
                            textView
                        } else {
                            val textView = TextView(context)
                            childViews.add(WeakReference(textView))
                            textView
                        }
                    }
                childTextView.text = s
                childTextView.setBackgroundColor(Color.RED)
                childTextView.setPadding(30, 0, 30, 0)
                if (!childTextView.hasOnClickListeners()) {
                    childTextView.setOnClickListener {
                        it as TextView
                        Toast.makeText(context, it.text, Toast.LENGTH_SHORT).show()
                    }
                }
                addView(childTextView)
            }
            if (collapseImageView == null) {
                collapseImageView = ImageView(context)
            }
            collapseImageView?.let { imageView ->
                imageView.setImageResource(R.drawable.icon_arrow_down)
                imageView.setBackgroundColor(Color.CYAN)
                if (!imageView.hasOnClickListeners()) {
                    imageView.setOnClickListener {
                        if (mOriginMeasuredParentHeight == 0) {
                            mOriginMeasuredParentHeight = measuredHeight
                        }
                        if (!it.isSelected) {//collapse
                            layoutParams.height = getOneLineHeight
                            Log.e("search", "getOneLineHeight:$getOneLineHeight")
                            this.layoutParams = layoutParams
                        } else {//expend
                            Log.e(
                                "search",
                                "mOriginMeasuredParentHeight:$mOriginMeasuredParentHeight"
                            )
                            layoutParams.height = mOriginMeasuredParentHeight
                            this.layoutParams = layoutParams
                        }
                        it.isSelected = !it.isSelected

                    }
                }
            }

            addView(collapseImageView)
            requestLayout()
            invalidate()
        }

    val getOneLineHeight: Int
        get() {
            val childAt = getChildAt(0)
            childAt?.let {
                return paddingTop + paddingBottom + it.measuredHeight + it.marginTop + it.marginBottom
            }
            return 0
        }

    /**
     *
     * for child
     */
    override fun generateDefaultLayoutParams(): LayoutParams {
        val marginLayoutParams =
            MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT)
        marginLayoutParams.topMargin = 10
        marginLayoutParams.leftMargin = 20
        marginLayoutParams.rightMargin = 20
        marginLayoutParams.bottomMargin = 10
        return marginLayoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeW = MeasureSpec.getSize(widthMeasureSpec)
        val sizeH = MeasureSpec.getSize(heightMeasureSpec)
        Log.e("sizeW", "$sizeW}")
        Log.e("sizeH", "$sizeH}")
        var widthUsedUp = 0
        var measuredLineHeight = 0
        var lineCount = 0
        for ((index, s) in textStrings.withIndex()) {//遍历以计算高度
            val childAt = getChildAt(index)
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec)
            widthUsedUp += paddingLeft +
                    childAt.measuredWidth +
                    childAt.marginLeft +
                    childAt.marginRight
            Log.e("width$index", "width:${childAt.measuredWidth} height:${childAt.measuredHeight}")
            if (index == 0) {
                measuredLineHeight += childAt.measuredHeight + childAt.marginTop + childAt.marginBottom
            }

            if (widthUsedUp > if (lineCount == 0) {
                    sizeW - rightMarginWidthForClearSpace - paddingRight //为点击下拉按钮腾出空间
                } else sizeW - paddingRight
            ) {
                lineCount++
                widthUsedUp = 0
                if (lineCount < 3) {
                    measuredLineCount = lineCount
                    measuredLineHeight += childAt.measuredHeight + childAt.marginTop + childAt.marginBottom
                } else {
                    break
                }
            }
        }
        val allHeight = measuredLineHeight + paddingTop + paddingBottom
        setMeasuredDimension(
            sizeW,
            if (measuredLineHeight == 0 || allHeight > sizeH) sizeH else allHeight
        )
    }

    var lineCount = 1
    val rect: Rect = Rect()
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        rect.set(l, t, r, b)
        Log.e("rect", "$rect ${rect.width()}")
        if (changed) {
            var nextItemLeft = l + paddingLeft
            var nextItemTop = t + paddingTop
            var right = r
            var bottom = b
            for (i in 0 until childCount) {
                val childAt = getChildAt(i)
                //计算下一个至右边，或者另起一行
                val rightAvailable = r - paddingRight
                if (nextItemLeft + childAt.measuredWidth + childAt.marginLeft + childAt.marginRight > if (lineCount == 1) (r - rightMarginWidthForClearSpace - paddingRight) else rightAvailable) {
                    lineCount++
                    if (lineCount == 2) {
                        val left = rightAvailable - childAt.measuredHeight
                        collapseImageView?.layout(
                            left,
                            nextItemTop + childAt.marginTop,
                            left + childAt.measuredHeight,
                            nextItemTop + childAt.marginTop + childAt.measuredHeight
                        )
                    }
                    nextItemLeft = l + paddingLeft//reset
                    nextItemTop += childAt.measuredHeight + childAt.marginTop + childAt.marginBottom
                }

                val ll = nextItemLeft + childAt.marginLeft
                val rr = ll + childAt.measuredWidth
                val tt = nextItemTop + childAt.marginTop
                val bb = tt + childAt.measuredHeight
                rect.set(ll, tt, rr, bb)
                if (childAt.measuredWidth != 0) {
                    childAt.layout(ll, tt, rr, bb)
                    Log.e("childCount$childCount childAt$i", "layout $rect ${rect.width()}")
                }
                if (lineCount > 3) {
                    break
                }

                //set next
                nextItemLeft += childAt.measuredWidth + childAt.marginLeft + childAt.marginRight
            }
        }
    }
}