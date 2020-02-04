package com.chetdeva.spinnerbindings.binding

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.iam_client.ui.utils.SpinnerExtensions
import com.iam_client.ui.utils.SpinnerExtensions.setSpinnerEntries
import com.iam_client.ui.utils.SpinnerExtensions.setSpinnerItemSelectedListener
import com.iam_client.ui.utils.SpinnerExtensions.setSpinnerValue

/**
 * Copyright (c) 2017 Fueled. All rights reserved.
 * @author chetansachdeva on 09/04/18
 */
class SpinnerBindings {

    @BindingAdapter("entries")
    fun Spinner.setEntries(entries: List<Any>?) {
        setSpinnerEntries(entries)
    }

    @BindingAdapter("onItemSelected")
    fun Spinner.setOnItemSelectedListener(itemSelectedListener: SpinnerExtensions.ItemSelectedListener?) {
        setSpinnerItemSelectedListener(itemSelectedListener)
    }

    @BindingAdapter("newValue")
    fun Spinner.setNewValue(newValue: Any?) {
        setSpinnerValue(newValue)
    }
}