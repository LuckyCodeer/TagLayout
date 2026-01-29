package com.yhw.taglayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlin.math.max

private const val TAG = "TagLayout===>"

/**
 * 标签布局
 * @author yhw
 */
class TagLayout : ViewGroup {
    private val mViewRectMap = mutableMapOf<View, MyRect>()
    private val mLineWidthMap = mutableMapOf<Int, Int>() //存放行数和距离右侧的宽度
    private var choiceMode = ChoiceMode.None.choiceMode //选择模式
    private var defChoicePosition: Int = 0 //单选时默认选中
    private lateinit var mAdapter: TagAdapter
    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null
    var onSingleCheckedChangeListener: OnSingleCheckedChangeListener? = null
    var onMultipleCheckedChangeListener: OnMultipleCheckedChangeListener? = null
    private var mScreenWidth = 0 //屏幕宽度
    private var mSingleChoiceSupportCancel = false //单选是否支持取消
    private var mMeasuredWidth = 0 //控件宽度
    private var mGravity = 0 //对齐方式
    private var mMaxLines = -1 //默认最大显示行数
    private var mLines = 0 //总行数
    private var mMaxHeight = 0 //限制最大显示行数时的最大高度

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TagLayout)
        choiceMode = ta.getInt(R.styleable.TagLayout_choiceMode, ChoiceMode.None.choiceMode)
        defChoicePosition = ta.getInt(R.styleable.TagLayout_defaultChoicePosition, 0)
        mSingleChoiceSupportCancel =
            ta.getBoolean(R.styleable.TagLayout_singleChoiceSupportCancel, false)
        mGravity = ta.getInt(R.styleable.TagLayout_gravity, GravityMode.Left.gravity)
        mMaxLines = ta.getInt(R.styleable.TagLayout_maxLines, -1)
        ta.recycle()

        val windowManager = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        this.mScreenWidth = displayMetrics.widthPixels
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = 0 //最终父布局宽度
        var height = 0 //最终父布局高度
        var lineWidth = 0 //行宽
        var lineHeight = 0 //行高
        Log.i(TAG, "widthSize is $widthSize  widthMode is $widthMode mScreenWidth:${mScreenWidth}")
        var lineNum = 0 //行数
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val marginLayoutParams: MarginLayoutParams =
                childView.layoutParams as MarginLayoutParams
            //子View的宽
            val childWidth =
                childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
            //子View的高
            val childHeight =
                childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin

            //判断是否需要换行
            if (lineWidth + childWidth > widthSize - paddingLeft - paddingRight) {
                lineNum ++
                Log.i(TAG, "lineWidth is $lineWidth  lineHeight is $lineHeight")
                //记录所有行中宽度最大的行
                width = max(width, lineWidth)
                //换行后重置行宽为第一个view的宽度
                lineWidth = childWidth
                //高度累加
                height += lineHeight
                //换行后行高重置为第一个view的高度
                lineHeight = childHeight
                //限制最大行数
                Log.i(TAG, "mMaxLines============ $lineNum   $mMaxLines")
                 if (mMaxLines != -1 && lineNum == mMaxLines) {
                     mMaxHeight = height
                 }
            } else {
                //宽度累加
                lineWidth += childWidth
                //记录每行view中高度最高的view为当前行高
                lineHeight = max(childHeight, lineHeight)
            }
            mLines = lineNum
            val srcLineWidth = if (mLineWidthMap[lineNum] != null) mLineWidthMap[lineNum]!! else 0
            mLineWidthMap[lineNum] = max(lineWidth, srcLineWidth)
            if (choiceMode == ChoiceMode.SingleChoice.choiceMode) {
                if (i in 0 until childCount && i == defChoicePosition)
                    childView.isSelected = true
            }
            val childLeft = lineWidth - childWidth + marginLayoutParams.leftMargin + paddingLeft
            var childRight = childView.measuredWidth + childLeft
            if (childRight > mScreenWidth) {
                childRight = mScreenWidth - marginLayoutParams.rightMargin - paddingRight
//                childRight = childView.measuredWidth
            }
            val childTop = height + marginLayoutParams.topMargin + paddingTop
            val childBottom =
                height + childHeight - marginLayoutParams.bottomMargin + paddingTop
            val rect = MyRect(childLeft, childTop, childRight, childBottom)
            rect.lineNum = lineNum
            Log.i(
                TAG, "onMeasure  left:${rect.left}  top:${rect.top} ," +
                        "right:${rect.right} ,bottom:${rect.bottom}   measuredWidth:${childView.measuredWidth}"
            )
            childView.isEnabled = isEnabled
            mViewRectMap[childView] = rect

            //最后一行处理
            if (i == childCount - 1) {
                width = max(lineWidth, width)
                height += lineHeight
            }
            Log.i(TAG, "lineCount============ $lineNum   $mMaxHeight")
        }
        mMeasuredWidth =
            if (widthMode == MeasureSpec.EXACTLY) widthSize else width + paddingLeft + paddingRight
        val measuredHeight =
            if (heightMode == MeasureSpec.EXACTLY) heightSize else height + paddingTop + paddingBottom
        Log.i(
            TAG,
            "measuredWidth  :${mMeasuredWidth}  measuredHeight:${measuredHeight}  width:${width} lineWidth:${lineWidth}"
        )

        setMeasuredDimension(max(mMeasuredWidth, width), if (mMaxHeight == 0) measuredHeight else mMaxHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.i(TAG, "=========onLayout========== $changed  $l  $t  $r  $b")
        var i = 0
        for ((childView, rect) in mViewRectMap) {
            val lineRightWidth = mLineWidthMap[rect.lineNum]
            var moveGap = 0
            if (mGravity == GravityMode.RIGHT.gravity) {
                if (lineRightWidth != null && lineRightWidth > 0) {
                    moveGap = mMeasuredWidth - lineRightWidth
                }
            } else if (mGravity == GravityMode.CENTER.gravity) {
                if (lineRightWidth != null && lineRightWidth > 0) {
                    moveGap = (mMeasuredWidth - lineRightWidth) / 2
                }
            }
            childView.layout(rect.left + moveGap, rect.top, rect.right + moveGap, rect.bottom)
            setItemListener(childView, i)
            i++
        }
    }

    /**
     * 给子view设置点击事件
     */
    private fun setItemListener(childView: View, position: Int) {
        if (choiceMode != ChoiceMode.None.choiceMode || (onItemClickListener != null || onItemLongClickListener != null)) {
            childView.setOnClickListener {
                changedCheckedItemView(position)
                if (onItemClickListener != null) {
                    onItemClickListener?.onItemClick(position, it)
                }
                if (choiceMode == ChoiceMode.SingleChoice.choiceMode) {
                    if (it.isSelected) {
                        this.defChoicePosition = position
                    } else {
                        this.defChoicePosition = -1
                    }
                    if (onSingleCheckedChangeListener != null) {
                        onSingleCheckedChangeListener?.onCheckedChanged(defChoicePosition)
                    }
                } else if (choiceMode == ChoiceMode.MultipleChoice.choiceMode) {
                    if (onMultipleCheckedChangeListener != null) {
                        onMultipleCheckedChangeListener?.onCheckedChanged(getCheckedList())
                    }
                }
            }

            if (onItemLongClickListener != null) {
                childView.setOnLongClickListener { v ->
                    onItemLongClickListener?.onItemLongClick(position, v)
                    true
                }
            }
        }
    }

    /**
     * 改变选中
     */
    private fun changedCheckedItemView(position: Int) {
        Log.i(TAG, "choiceMode is $choiceMode")
        var index = 0
        for ((view, _) in mViewRectMap) {
            if (position == -1) {
                view.isSelected = false
                continue
            }
            if (choiceMode == ChoiceMode.SingleChoice.choiceMode) {
                if (mSingleChoiceSupportCancel) {
                    if (index == position) {
                        view.isSelected = !view.isSelected
                    } else {
                        view.isSelected = false
                    }
                } else {
                    view.isSelected = index == position
                }
            } else if (choiceMode == ChoiceMode.MultipleChoice.choiceMode) {
                if (index == position) {
                    view.isSelected = !view.isSelected
                }
            } else {
                view.isSelected = false
            }
            index++
        }
    }

    /**
     * 获取多选 选中列表
     */
    fun getCheckedList(): MutableList<Int> {
        val checkedList = mutableListOf<Int>()
        var index = 0
        for ((view, _) in mViewRectMap) {
            if (view.isSelected) {
                checkedList.add(index)
            }
            index++
        }
        return checkedList
    }

    /**
     * 获取单选 选中索引
     */
    fun getCheckedPosition(): Int {
        return defChoicePosition
    }

    /**
     * 设置数据适配器
     */
    fun setAdapter(adapter: TagAdapter) {
        mAdapter = adapter
        changedAdapter()
    }

    private fun changedAdapter() {
        removeAllViews()
        mViewRectMap.clear()
        for (i in 0 until mAdapter.getItemCount()) {
            val itemView = mAdapter.onCreateView(this)
            mAdapter.onBindView(itemView, i)
            addView(itemView)
        }
    }

    fun getAdapter(): TagAdapter {
        return mAdapter
    }

    /**
     * 设置选择模式
     * @see ChoiceMode
     */
    fun setChoiceMode(mode: ChoiceMode) {
        this.choiceMode = mode.choiceMode
        if (mode.choiceMode == ChoiceMode.SingleChoice.choiceMode) {
            defChoicePosition = 0
            changedCheckedItemView(defChoicePosition)
            if (onSingleCheckedChangeListener != null) {
                onSingleCheckedChangeListener?.onCheckedChanged(defChoicePosition)
            }
        } else {
            changedCheckedItemView(-1)
        }
        var i = 0
        for ((childView, _) in mViewRectMap) {
            setItemListener(childView, i)
            i++
        }
    }

    /**
     * 单选模式下，设置默认选择项
     */
    fun setDefaultChoicePosition(position: Int) {
        this.defChoicePosition = position
        if (choiceMode == ChoiceMode.SingleChoice.choiceMode) {
            changedCheckedItemView(defChoicePosition)
        }
    }

    fun getChoiceMode(): Int {
        return choiceMode
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        for ((childView, _) in mViewRectMap) {
            childView.isEnabled = enabled
        }
    }

    /**
     * 获取总行数
     */
    fun getLines(): Int {
        return mLines + 1
    }

    /**
     * 获取显示的item数量
     */
    fun getShowItemSize(): Int {
        return mViewRectMap.size
    }

    /**
     * 设置最大行数
     */
    fun setMaxLines(maxLines: Int) {
        this.mMaxLines = maxLines
        this.mMaxHeight = 0
        requestLayout()
    }

    /**
     * 展示全部
     */
    fun showAll() {
        setMaxLines(-1)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int, view: View)
    }

    interface OnSingleCheckedChangeListener {
        fun onCheckedChanged(position: Int)
    }

    interface OnMultipleCheckedChangeListener {
        fun onCheckedChanged(positionList: MutableList<Int>)
    }

    /**
     * 选择模式
     */
    enum class ChoiceMode(var choiceMode: Int) {
        /**
         * 非选择模式
         */
        None(0),

        /**
         * 单选模式
         */
        SingleChoice(1),

        /**
         * 多选模式
         */
        MultipleChoice(2);
    }

    /**
     * 对齐方式
     */
    enum class GravityMode(var gravity: Int) {
        /**
         * 居左对齐
         */
        Left(0),

        /**
         * 居中对齐
         */
        CENTER(1),

        /**
         * 居右对齐
         */
        RIGHT(2);
    }
}