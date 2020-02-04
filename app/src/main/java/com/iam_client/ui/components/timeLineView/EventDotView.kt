package com.iam_client.ui.components.timeLineView

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import com.iam_client.R


class EventDotView  : View {
    constructor (context: Context):super(context) { init(context,null)}
    constructor (context: Context, attrs: AttributeSet?):super(context,attrs) { init(context,attrs)}
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context, attrs, defStyleAttr) { init(context,attrs)}
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
            super(context, attrs, defStyleAttr, defStyleRes){ init(context,attrs)}

    private fun init(context: Context, attrs: AttributeSet?){
        val typedArray : TypedArray = context.obtainStyledAttributes(attrs, R.styleable.EventDotView)
        try {
            iconColor = typedArray.getColor(R.styleable.EventDotView_icon_color,0)
            icon = typedArray.getResourceId(R.styleable.EventDotView_icon,R.drawable.phone)
            drawCirclePaintColor = typedArray.getColor(R.styleable.EventDotView_circle_background_color,0)
            circlePaddingTop = typedArray.getDimension(R.styleable.EventDotView_circle_top_padding,20f)
            topLinePaintColor =  typedArray.getColor(R.styleable.EventDotView_top_line_color,Color.GRAY)
            bottomLinePaintColor =  typedArray.getColor(R.styleable.EventDotView_bottom_line_color,Color.GRAY)
            drawTopLine = typedArray.getBoolean(R.styleable.EventDotView_top_line_visible,true)
            drawBottomLine = typedArray.getBoolean(R.styleable.EventDotView_bottom_line_visible,true)
            circleRadius =  typedArray.getDimension(R.styleable.EventDotView_circle_radius,30f)
            strokeWidth =  typedArray.getDimension(R.styleable.EventDotView_stroke_width,30f)

        }
        finally {
            typedArray.recycle()
        }
    }

    var circlePaddingTop = 0f
    var drawBottomLine = true
    var drawTopLine = true
    var circleRadius = 30f
    var strokeWidth =10f
        set(value) {
            topLinePaint.strokeWidth = value
            bottomLinePaint.strokeWidth = value

            field = value
        }

    fun setTint(d: Drawable, color: Int): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(d)
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }
    var iconDraw:Drawable?=null
    var iconColor :Int?= null
        set(value) {
            if(value!=null)
                iconDraw?.setTint(value)
            field = value }

    var icon :Int?= null
        set(value) {
            if(value!=null){
                iconDraw = resources.getDrawable(value, null)
                if(iconColor!=null)
                    iconDraw?.setTint(value)
            }
            field = value }
    var topLinePaintColor : Int = 0
        set(value) {
            topLinePaint.color = value
            field = value }
    var bottomLinePaintColor : Int = 0
        set(value) {
            bottomLinePaint.color = value
            field = value }
    var drawCirclePaintColor : Int = 0
        set(value) {
            drawCirclePaint.color = value
            field = value }



    private val drawCirclePaint = Paint().apply {
        color = Color.parseColor("#466686")
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val topLinePaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.SQUARE
    }
    private val bottomLinePaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.SQUARE
    }
    private val drawIconPaint = Paint()

    private val iconRect = Rect()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val yPos = paddingTop.toFloat()
        val xPos = 0f
        //circle center pos
        val circleX = xPos + circleRadius
        val circleY = yPos +circlePaddingTop + circleRadius
        if(drawTopLine)
            canvas?.drawLine(circleX,yPos,circleX, circleY-circleRadius,topLinePaint)
        if(drawBottomLine)
            canvas?.drawLine(circleX,circleY+circleRadius,circleX, height.toFloat()-paddingBottom,bottomLinePaint)

        canvas?.drawCircle(circleX,circleY,circleRadius, drawCirclePaint)
        val mRadius =(0.7 * circleRadius).toInt() //inside circle
        val r = iconRect.apply {
            top = circleY.toInt() -mRadius
            left = circleX.toInt() - mRadius
            bottom  = top + 2*mRadius
            right = left + 2*mRadius
        }
        if(this.iconDraw !=null){
            this.iconDraw?.setBounds(r.left, r.top, r.right, r.bottom);
            this.iconDraw?.draw(canvas!!)
        }

    }

    private fun calcDesiredMeasure():Pair<Int,Int>{
        return Pair(circleRadius.toInt()*2 ,circleRadius.toInt()*2 + circlePaddingTop.toInt() + paddingTop +paddingBottom )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)


        val(desiredWidth, desiredHeight) = calcDesiredMeasure()

        val width  = when(widthMode){
            MeasureSpec.EXACTLY->widthSize //Must be this size
            MeasureSpec.AT_MOST->Math.min(desiredWidth, widthSize)//Can't be bigger than...
            else -> desiredWidth //Be whatever you want
        }

        val height = when(heightMode){
            MeasureSpec.EXACTLY->heightSize //Must be this size
            MeasureSpec.AT_MOST->Math.min(desiredHeight, heightSize)//Can't be bigger than...
            else -> desiredHeight //Be whatever you want
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }



}