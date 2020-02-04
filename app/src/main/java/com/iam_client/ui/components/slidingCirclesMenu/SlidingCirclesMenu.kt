package com.iam_client.ui.components.slidingCirclesMenu

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.iam_client.R
import org.jetbrains.anko.backgroundColor
import kotlin.math.abs
import android.util.TypedValue
import android.util.DisplayMetrics





class SlidingCirclesMenu : RelativeLayout {
    constructor (context: Context):super(context) { init(context,null)}
    constructor (context: Context, attrs: AttributeSet?):super(context,attrs) { init(context,attrs)}
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context, attrs, defStyleAttr) { init(context,attrs)}
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
            super(context, attrs, defStyleAttr, defStyleRes){ init(context,attrs)}
    private var view : View? = null

    private fun init(context: Context, attrs: AttributeSet?){
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.sliding_circles_menu, this, true)

        view?.backgroundColor = Color.TRANSPARENT
// Set the ViewPager adapter
        val adapter = WizardPagerAdapter()
         pager = findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = adapter
        pager.offscreenPageLimit=5
        pager.currentItem = 2
       //val margin  = view!!.width *5f

        //pager.pageMargin  = dipToPixels(context,-margin).toInt()
        val transformation = DepthTransformation()
        pager.setPageTransformer(true, transformation)
//        (buttonsContainer.children.filter { button-> (button).tag == "ScrollTopTag" }.first()  as Button)
//            .apply { text = "test text" }.requestFocus()

    }
    lateinit var pager :ViewPager
    var margin : Float? =null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        if(margin == null){
            margin  =parentWidth *0.2f
            pager.pageMargin  = dipToPixels(context,-margin!!).toInt()
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun dipToPixels(context: Context, dipValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    internal inner class WizardPagerAdapter : PagerAdapter() {

        override fun instantiateItem(collection: View, position: Int): Any {

            var resId = 0
            when (position) {
                0 -> resId = R.id.page_1
                1 -> resId = R.id.page_2
                2 -> resId = R.id.page_3
                3 -> resId = R.id.page_4
                4 -> resId = R.id.page_5


            }
            return findViewById(resId)
        }

        override fun getCount(): Int {
            return 5
        }

        override fun getPageWidth(position: Int): Float {
            return 1f
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1 as View
        }




    }
    internal inner class DepthTransformation : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {

            if(position >= -1 && position <=1){
                val p = abs(position)
                //page.alpha =  1 - Math.abs(p)
                page.scaleX = 1 - Math.abs(p)
                page.scaleY = 1 - Math.abs(p)
            }


            else if (position < -1) {    // [-Infinity,-1)
                // This page is way off-screen to the left.
             //   page.alpha = 0f
//
//            } else if (position <= 0) {    // [-1,0]
//                page.alpha = 1f
//                page.translationX = 0f
//                page.scaleX = 1f
//                page.scaleY = 1f
//
//            } else if (position <= 1) {    // (0,1]
//                page.translationX = -position * page.width
//                page.alpha =  1 - Math.abs(position)
//                page.scaleX = 1 - Math.abs(position)
//                page.scaleY = 1 - Math.abs(position)

            } else {    // (1,+Infinity]
                // This page is way off-screen to the right.
               // page.alpha = 0f

            }


        }
    }

}