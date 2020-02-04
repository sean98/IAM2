package com.iam_client.utills

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.iam_client.repostories.data.Salesman

object CustomBindingAdapter {
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun isVisible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter(value = ["android:entries", "android:value", "android:adapter"], requireAll = false)
    fun Spinner.setSpinnerEntries(entries: List<Any>?, value: Any?, stringAdapter: ((Any) -> String)?) {
        if (entries != null) {
            if (adapter==null || (adapter as SpinnerAutoBindAdapter).items != entries) {
                val arrayAdapter = SpinnerAutoBindAdapter(
                    context,
                    entries,
                    stringAdapter//null//spinner.tag as? ((Any) -> String)?
                )
                adapter = arrayAdapter
            }
            if (value!=null) {
                val pos = (adapter as SpinnerAutoBindAdapter).getItemPosition(value)
                setSelection(pos)
            }
        }
    }

//    @JvmStatic
//    @BindingAdapter("android:value")
//    fun setSpinnerValue(spinner: Spinner, value: Any?) {
//        if (value!=null && spinner.adapter!=null) {
//            val pos = (spinner.adapter as SpinnerAutoBindAdapter).getItemPosition(value)
//            spinner.setSelection(pos)
//        }
//    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:value", event = "android:selectedItemPositionAttrChanged")
    fun getSpinnerValue(spinner: Spinner): Any? {
        val adapter = spinner.adapter
        if (adapter is SpinnerAutoBindAdapter)
            return adapter.getItemValue(spinner.selectedItemPosition)
        return spinner.selectedItem
    }


    private class SpinnerAutoBindAdapter(context: Context,
                                val items: List<Any>,
                                itemConverter: ((Any) -> String)?
    ) : ArrayAdapter<String>(context,
        android.R.layout.simple_spinner_dropdown_item,
        items.map(itemConverter ?: Any::toString)) {

        init {
            setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item)
        }


        fun getItemValue(position: Int): Any? {
            return items[position]
        }

        fun getItemPosition(item: Any): Int {
            return items.indexOf(item)
        }
    }
}