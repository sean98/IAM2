package com.iam_client.ui.main


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

import com.iam_client.R
import com.iam_client.ui.components.timeLineView.TimeLineRecord
import com.iam_client.utills.reactive.observe
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.MainEntryViewModel
import com.iam_client.viewModels.main.MainEntryViewModelFactory
import kotlinx.android.synthetic.main.fragment_entry.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

//DEMO ENTRY
class Entry : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModel by lazy {
        val factory: MainEntryViewModelFactory by instance()
        ViewModelProviders
            .of(this, factory)
            .get(MainEntryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.profilePictureURL.observe(this) {
            if (it == null)
                profile_image.imageResource = R.drawable.user_mock_img
            else
                Glide.with(this)
                    .load(it)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .error(android.R.drawable.stat_notify_error)
                            .placeholder(R.drawable.user_mock_img)
                    )
                    .into(profile_image)
        }
        viewModel.errorMessage.observeEvent(this) { toast("error: ${it.message}") }

        chart.setTouchEnabled(false)
        chart.setDrawGridBackground(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(true)

        chart.xAxis.apply {
            setDrawGridLines(false)

        }

        chart.axisLeft.apply {
          setDrawGridLines(false)
        }

        val values = arrayListOf<Entry>()

        for (i in 0 until 12) {
            val v= (Math.random() * 100000).toFloat() - 30
            values.add(Entry(i.toFloat(), v))
        }
        val set = LineDataSet(values, "DataSet 1")
        set.setDrawIcons(false)
        // black lines and points
        set.color = Color.BLACK
        set.setCircleColor(Color.BLACK)

        // line thickness and point size
        set.lineWidth = 1f
        set.circleRadius = 3f

        // draw points as solid circles
        set.setDrawCircleHole(false)

        // set the filled area
        set.setDrawFilled(true)
        set.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }

        set.fillColor = Color.RED//TODO Drawable -set.setFillDrawable(drawable)

        val dataSets = listOf(set)
        val data = LineData(dataSets)

        // set data
        chart.data = data

        chart.animateY(1000, Easing.EaseInCirc)
    }


    var eventList: List<TimeLineRecord.Event>? = listOf(
        TimeLineRecord.Event(R.drawable.phone, "Lior Itzhak", "phone call"),
        TimeLineRecord.Event(R.drawable.email, "Sean Goldfarb", "Order : 154668855"),
        TimeLineRecord.Event(R.drawable.cancel, "SRS LDT DEMO", "Cancel Invoice : 1545131321")


        //        test1.eventList = eventList
//        test1.mainTitleText = "04-06-1992 main title"
//        test2.eventList = eventList
//        test2.mainTitleText = "06-04-1991 sec title"
//        //test3.eventList = eventList
//        //test3.mainTitleText = "06-05-1991 no style"
    )
}
