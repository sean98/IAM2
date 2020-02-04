package com.chetdeva.spinnerbindings.binding
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.iam_client.ui.utils.SpinnerExtensions.getSpinnerValue
import com.iam_client.ui.utils.SpinnerExtensions.setSpinnerInverseBindingListener
import com.iam_client.ui.utils.SpinnerExtensions.setSpinnerValue

/**
 * Copyright (c) 2017 Fueled. All rights reserved.
 * @author chetansachdeva on 10/04/18
 */
class InverseSpinnerBindings {

    @BindingAdapter("selectedValue")
    fun Spinner.setSelectedValue(selectedValue: Any?) {
        setSpinnerValue(selectedValue)
    }

    @BindingAdapter("selectedValueAttrChanged")
    fun Spinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
        setSpinnerInverseBindingListener(inverseBindingListener)
    }

    companion object InverseSpinnerBindings {

        @JvmStatic
        @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
        fun Spinner.getSelectedValue(): Any? {
            return getSpinnerValue()
        }
    }
}