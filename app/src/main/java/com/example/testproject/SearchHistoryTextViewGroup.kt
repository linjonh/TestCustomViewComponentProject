package com.example.testproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
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
 * created by
 * @author jaysen.lin@foxmail.com
 * @since 2022/11/7
 *
 **/
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
    val childViews: MutableList<WeakReference<TextView>> = mutableListOf()
    var collapseImageView: ImageView? = null
    var measuredLineCount: Int = 1
    var mOriginMeasuredParentHeight: Int = 0
    var textColor: Int = Color.BLACK
    var textSize: Float = 0f
    var textMargin: Int = 0
    var textPadding: Int = 0
    var textBackgroundColor: Int = 0
    var textBackgroundDrawable: Drawable? = null

    init {
        rightMarginWidthForClearSpace =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics)
                .toInt()
        textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 14f, resources.displayMetrics)
        val typeArray = resources.obtainAttributes(attrs, R.styleable.SearchHistoryViewGroup)
        try {
            textBackgroundColor =
                typeArray.getColor(R.styleable.SearchHistoryViewGroup_textItemBackground, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            textBackgroundDrawable =
                typeArray.getDrawable(R.styleable.SearchHistoryViewGroup_textItemBackground)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        textColor = typeArray.getColor(
            R.styleable.SearchHistoryViewGroup_textItemColor, textColor
        )
        textSize = typeArray.getDimension(R.styleable.SearchHistoryViewGroup_textItemSize, 14f)
        textMargin =
            typeArray.getDimensionPixelSize(R.styleable.SearchHistoryViewGroup_textItemMargin, 0)
        textPadding =
            typeArray.getDimensionPixelSize(R.styleable.SearchHistoryViewGroup_textItemPadding, 0)
        val string = typeArray.getString(R.styleable.SearchHistoryViewGroup_exampleString)
        Log.e("exampleString", string ?: "")
        string?.split(",")?.let {
            Log.e("split", it.toString())
            textStrings = (it as MutableList<String>)
        }
        typeArray.recycle()
    }


    var textStrings: MutableList<String> = mutableListOf()
        set(value) {
            if (field == null) {
                field = value
            } else {
                if (value == field) {
                    Log.e("field", "the same")
                    return
                }
                field.clear()
                field.addAll(value)
            }
            Log.e("field", field.toString())
            addViews()
        }

    fun addViews() {
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
            if (textBackgroundColor != 0) {
                if (childTextView.background == null) {
                    childTextView.setBackgroundColor(textBackgroundColor)
                    Log.e("${childTextView.text} background", "settled")
                }
            }
            if (textBackgroundDrawable != null) {
                if (childTextView.background == null) {
                    childTextView.background =
                        textBackgroundDrawable!!.constantState!!.newDrawable()
                    Log.e("${childTextView.text} background", "settled")
                }
            }
            childTextView.setTextColor(textColor)
            childTextView.setPadding(30, 0, 30, 0)
            childTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
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
            Log.e("collapseImageView", "create")

        }
        collapseImageView?.let { imageView ->
            imageView.setImageResource(R.drawable.icon_arrow_down)
            if (imageView.background == null) {
                imageView.background = textBackgroundDrawable!!.constantState!!.newDrawable()
            }
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
        Log.e("sizeW", "$sizeW")
        Log.e("sizeH", "$sizeH")
        var widthUsedUp = 0
        var measuredLineHeight = 0
        var lineCount = 1
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

            if (widthUsedUp > if (lineCount == 1) {
                    sizeW - rightMarginWidthForClearSpace - paddingRight //为点击下拉按钮腾出空间
                } else sizeW - paddingRight
            ) {
                lineCount++
                widthUsedUp = 0
                if (lineCount <= 3) {
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        for (i in 0 until childCount) {
//            val childAt = getChildAt(i)
//            val bounds = childAt.background?.bounds?.set(childAt.left,childAt.top,childAt.right,childAt.bottom)
//            Log.e("bounds $i", childAt.background?.bounds.toString())
//            childAt.draw(canvas)
////            childAt.background?.draw(canvas)
//        }
    }

    val rect: Rect = Rect()
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        rect.set(l, t, r, b)
        var lineCount = 1
        Log.e("rect", "$rect ${rect.width()} changed:$changed measuredLineCount:$measuredLineCount")
        collapseImageView?.visibility = if (measuredLineCount == 1) {
            INVISIBLE
        } else {
            VISIBLE
        }
        Log.e("changed", " changed:$changed childCount$childCount")
//        if (changed) {
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
                    Log.e("collapseImageView", collapseImageView.toString())
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
            } else {
                Log.e("childCount$childCount childAt$i", "measuredWidth =0")

            }
            if (lineCount > 3) {
                break
            }

            //set next
            nextItemLeft += childAt.measuredWidth + childAt.marginLeft + childAt.marginRight
//            }
        }
    }
}