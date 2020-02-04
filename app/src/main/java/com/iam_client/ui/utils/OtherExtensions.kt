package com.iam_client.ui.utils

import androidx.databinding.Observable
import androidx.databinding.ObservableField


/**
 * Copyright (c) 2017 Fueled. All rights reserved.
 * @author chetansachdeva on 10/04/18
 */

inline fun <reified T> Observable.get(): T = (this as ObservableField<T>).get()!!

fun Int.populateList(): List<Int> = (1..this).toList()
