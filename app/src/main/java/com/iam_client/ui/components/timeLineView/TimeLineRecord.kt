package com.iam_client.ui.components.timeLineView

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.iam_client.R
import kotlinx.android.synthetic.main.time_line_record_layout.view.*
import org.jetbrains.anko.textColor
import androidx.core.content.ContextCompat


class TimeLineRecord : LinearLayout {
    constructor (context: Context) : super(context) {
        init(context, null)
    }

    constructor (context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    private var view: View? = null
    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.time_line_record_layout, this, true)

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeLineRecord)
        try {
            mainTitleText = typedArray.getString(R.styleable.TimeLineRecord_main_title_text)
            mainTitleTextColor = typedArray.getColor(R.styleable.TimeLineRecord_main_title_text_color, Color.BLACK)
            mainTitleVisibility = if (typedArray.getBoolean(R.styleable.TimeLineRecord_main_title_visible, true))
                View.VISIBLE else View.GONE
            mainTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.TimeLineRecord_main_title_text_size,50)
            mainTitleBackgroundColor = typedArray.getColor(R.styleable.TimeLineRecord_main_title_background_color,Color.parseColor("#C5E5E8"))

            linesColor = typedArray.getColor(R.styleable.TimeLineRecord_line_color, Color.BLACK)
            linesWidth = typedArray.getDimensionPixelSize(R.styleable.TimeLineRecord_line_width,2)

            eventsTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.TimeLineRecord_events_title_text_size,60)
            eventsTitleTextColor = typedArray.getColor(R.styleable.TimeLineRecord_events_title_text_color, Color.BLACK)
            eventsDescriptionsTextSize = typedArray.getDimensionPixelSize(R.styleable.TimeLineRecord_events_descriptions_text_size,50)
            eventsDescriptionsTextColor = typedArray.getColor(R.styleable.TimeLineRecord_events_descriptions_text_color, Color.BLACK)

            iconsColor =  typedArray.getColor(R.styleable.TimeLineRecord_icons_color, Color.BLACK)
            iconsBackgroundColor =  typedArray.getColor(R.styleable.TimeLineRecord_icon_circles_color, Color.RED)


        } finally {
            typedArray.recycle()
        }
    }


    fun EventView.mapFromModel(eventModel: Event): EventView {
        eventBodyText = eventModel.description
        eventTitleText = eventModel.title
        icon = eventModel.icon
        return this
    }
    private fun EventView.applyAttributes(): EventView {
        this.topLinePaintColor =  linesColor
        this.bottomLinePaintColor =  linesColor
        this.lineWidth =  linesWidth
        this.eventTitleTextColor =  eventsTitleTextColor
        this.eventTitleTextSize =  eventsTitleTextSize
        this.eventDescriptionTextColor =  eventsDescriptionsTextColor
        this.eventDescriptionTextSize =  eventsDescriptionsTextSize
        this.iconColor =  iconsColor
        return this
    }
    var eventList: List<Event>? = null
        set(value) {
            //eventsContainer.removeAllViews()
            if (value != null) {
                if (value.isNotEmpty()) {
                    val firstEventView = EventView(context).mapFromModel(value[0]).applyAttributes()
                    firstEventView.drawTopLine = false
                    if (0 == value.lastIndex)
                        firstEventView.drawBottomLine = false
                    eventsContainer.addView(firstEventView)

                    if (value.size > 2) {
                        value.subList(1, value.lastIndex).forEach { event ->
                            val eventView = EventView(context).mapFromModel(event).applyAttributes()
                            eventsContainer.addView(eventView)
                        }
                    }
                    if (value.size != 1) {
                        val lastEventView = EventView(context).mapFromModel(value[value.lastIndex]).applyAttributes()
                        lastEventView.drawBottomLine = false
                        eventsContainer.addView(lastEventView)
                    }
                }
            }
            field = value
        }


    data class Event(val icon: Int, val title: String, val description: String)


    var mainTitleTextColor: Int = Color.BLACK
        set(value) {
            mainTitle.textColor = value
            field = value
        }
    var mainTitleTextSize: Int = 50
        set(value) {
            mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,value.toFloat())
            field = value
        }
    var mainTitleText: String? = null
        set(value) {
            mainTitle.text = value
            field = value
        }
    var mainTitleVisibility = View.VISIBLE
        set(value) {
            mainTitle.visibility = value
            field = value
        }

    var mainTitleBackgroundColor : Int = 0
    set(value){
        val drawable =  ContextCompat.getDrawable(context,R.drawable.rounded_corner)
        drawable?.setColorFilter(value, PorterDuff.Mode.SRC_ATOP)
        mainTitle.background =drawable
        field = value
    }




    var linesColor : Int = Color.BLACK
    set(value){
        (0 until eventsContainer.childCount).forEach { i->
            val eventView = eventsContainer.getChildAt(i) as EventView
            eventView.topLinePaintColor = value
            eventView.bottomLinePaintColor = value
        }
        field = value
    }
    var linesWidth : Int = Color.BLACK
        set(value){
            (0 until eventsContainer.childCount).forEach { i->
                val eventView = eventsContainer.getChildAt(i) as EventView
                eventView.lineWidth = value
            }
            field = value
        }




    var eventsTitleTextColor: Int = Color.BLACK
        set(value) {
            (0 until eventsContainer.childCount).forEach { i ->
                val eventView = eventsContainer.getChildAt(i) as EventView
                eventView.eventTitleTextColor = value
            }
            field = value
        }
    var eventsTitleTextSize: Int = 50
            set(value) {
                (0 until eventsContainer.childCount).forEach { i ->
                    val eventView = eventsContainer.getChildAt(i) as EventView
                    eventView.eventTitleTextSize = value
                }
                field = value
            }

    var eventsDescriptionsTextColor: Int = Color.BLACK
        set(value) {
            (0 until eventsContainer.childCount).forEach { i ->
                val eventView = eventsContainer.getChildAt(i) as EventView
                eventView.eventDescriptionTextColor = value
            }
            field = value
        }
    var eventsDescriptionsTextSize: Int = 50
        set(value) {
            (0 until eventsContainer.childCount).forEach { i ->
                val eventView = eventsContainer.getChildAt(i) as EventView
                eventView.eventDescriptionTextSize = value
            }
            field = value
        }

    var iconsColor: Int = Color.BLACK
        set(value) {
            (0 until eventsContainer.childCount).forEach { i ->
                val eventView = eventsContainer.getChildAt(i) as EventView
                eventView.iconColor = value
            }
            field = value
        }
    var iconsBackgroundColor: Int = Color.RED
        set(value) {
            (0 until eventsContainer.childCount).forEach { i ->
                val eventView = eventsContainer.getChildAt(i) as EventView
                eventView.iconCircleBackgroungColor = value
            }
            field = value
        }

}