package com.iam_client.ui.main.documents

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.iam_client.R
import com.iam_client.repostories.data.docs.DocItem
import kotlinx.android.synthetic.main.doc_item_record.view.*
import com.iam_client.ui.utils.Formatter.formatMoneyString
import com.iam_client.ui.utils.Formatter.formatQuantityString




class DocItemView : FrameLayout {

    constructor (context: Context ) : super(context) {
        init(context, null)
    }

    constructor (context: Context ,root:ViewGroup) : super(context) {
        init(context, null)
    }



    constructor (context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private var view: View? = null

    private fun init(context: Context, attrs: AttributeSet?) {
        view = inflate(getContext(),R.layout.doc_item_record,null)
        addView(view)

        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

    }


    var item: DocItem? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            value?.apply {
                view!!.itemCodeText.text = code
                view!!.numTextField.text = "$visualOrder"
                view!!.descriptionTextField.background = null
                view!!.descriptionTextField.setText(description)
                view!!.totalTextField.setText(totalPrice.formatMoneyString(currency))
                view!!.detailsTextField.visibility = when (details.isNullOrBlank()) {
                    true -> View.GONE
                    false -> View.VISIBLE
                }
                view!!.detailsTextField.setText(details)
                view!!.commentsTextField.visibility = when (comments.isNullOrBlank()) {
                    true -> View.GONE
                    false -> View.VISIBLE
                }
                view!!.commentsTextField.setText(comments)
                view!!.quantityTextField.visibility = when (quantity) {
                    null -> View.GONE
                    else -> View.VISIBLE
                }
                view!!.quantityTextField.setText(quantity.formatQuantityString())
                view!!.priceQuantityTextField.setText(pricePerQuantity.formatMoneyString(currency))


                if (properties.containsKey("units")) {
                    view!!.unitTextField.visibility = View.VISIBLE
                    view!!.unitTextField.setText(properties["units"].toString())
                }
                if (properties.containsKey("width")) {
                    view!!.widthTextField.visibility = View.VISIBLE
                    view!!.widthTextField.setText(properties["width"].toString())
                }
                if (properties.containsKey("height")) {
                    view!!.heightTextField.visibility = View.VISIBLE
                    view!!.heightTextField.setText(properties["height"].toString())
                }
                if (properties.containsKey("length")) {
                    view!!.lengthTextField.visibility = View.VISIBLE
                    view!!.lengthTextField.setText(properties["length"].toString())
                }


            }
            field = value
        }
}