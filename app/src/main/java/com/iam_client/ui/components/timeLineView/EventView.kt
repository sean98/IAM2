package com.iam_client.ui.components.timeLineView

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.event_view.view.*
import android.view.View
import android.view.LayoutInflater
import com.iam_client.R
import org.jetbrains.anko.textColor

class EventView: FrameLayout  {
    constructor (context: Context):super(context) { init(context,null)}
    constructor (context: Context, attrs: AttributeSet?):super(context,attrs) { init(context,attrs)}
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context, attrs, defStyleAttr) { init(context,attrs)}
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
            super(context, attrs, defStyleAttr, defStyleRes){ init(context,attrs)}

    private fun init(context: Context, attrs: AttributeSet?){
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.event_view, this, true)

        val typedArray : TypedArray = context.obtainStyledAttributes(attrs, R.styleable.EventView)
        try {
            eventTitleText =  typedArray.getString(R.styleable.EventView_title_text)
            eventTitleTextColor =  typedArray.getColor(R.styleable.EventView_event_title_text_color, Color.BLACK)
            eventTitleTextSize =typedArray.getDimensionPixelSize(R.styleable.EventView_title_text_size,50)

            eventBodyText =  typedArray.getString(R.styleable.EventView_description_text)
            eventDescriptionTextColor =  typedArray.getColor(R.styleable.EventView_event_description_text_color, Color.BLACK)
            eventDescriptionTextSize =typedArray.getDimensionPixelSize(R.styleable.EventView_description_text_size,50)
            icon = typedArray.getResourceId(R.styleable.EventView_icon,R.drawable.phone)
            iconColor =  typedArray.getColor(R.styleable.EventView_icon_color, Color.BLUE)
            iconCircleBackgroungColor = typedArray.getColor(R.styleable.EventView_circle_background_color,0)
            circlePaddingTop = typedArray.getDimension(R.styleable.EventDotView_circle_top_padding,-1f)

            topLinePaintColor =  typedArray.getColor(R.styleable.EventView_bottom_line_color, Color.GRAY)
            bottomLinePaintColor =  typedArray.getColor(R.styleable.EventView_bottom_line_color, Color.GRAY)

            lineWidth =  typedArray.getDimensionPixelSize(R.styleable.EventView_line_width, 2)

            drawTopLine = typedArray.getBoolean(R.styleable.EventView_top_line_visible,true)
            drawBottomLine  = typedArray.getBoolean(R.styleable.EventView_bottom_line_visible,true)
        }
        finally {
            typedArray.recycle()
        }
    }

    private var view :View ? = null
    var eventBodyText : String? = null
        set(value) {
            body?.text = value
            field = value }

    var eventTitleText : String? = null
        set(value) {
            title?.text = value
            field = value }
    var eventTitleTextColor : Int = Color.BLACK
        set(value) {
            title?.textColor = value
            field = value }
    var eventTitleTextSize : Int = 50
        set(value) {
            title?.setTextSize(TypedValue.COMPLEX_UNIT_PX,value.toFloat())
            field = value }
    var eventDescriptionTextColor : Int =  Color.BLACK
        set(value) {
            body?.textColor = value
            field = value }
    var eventDescriptionTextSize : Int = 50
        set(value) {
            body?.setTextSize(TypedValue.COMPLEX_UNIT_PX,value.toFloat())
            field = value }
    var icon :Int?= null
        set(value) {
            if(value!=null)
                timeLine?.icon = value
            field = value }
    var iconColor :Int?= null
        set(value) {
            if(value!=null)
                timeLine?.iconColor = value
            field = value }

    var topLinePaintColor : Int = 0
        set(value) {
            timeLine?.topLinePaintColor = value
            field = value }
    var bottomLinePaintColor : Int = 0
        set(value) {
            timeLine?.bottomLinePaintColor = value
            field = value }

    var lineWidth: Int = 0
    set(value) {
        timeLine?.strokeWidth = value.toFloat()
        field = value }

    var iconCircleBackgroungColor : Int = 0
        set(value) {
            timeLine?.drawCirclePaintColor = value
            field = value }

    var drawTopLine : Boolean = true
        set(value) {
            timeLine?.drawTopLine = value
            field = value }
    var drawBottomLine : Boolean = true
        set(value) {
            timeLine?.drawBottomLine = value
            field = value }
    var circlePaddingTop : Float? = null
        set(value) {
            if (value != null && value > -1) {
                timeLine?.circlePaddingTop = value
                field = value
            }
        }


    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}