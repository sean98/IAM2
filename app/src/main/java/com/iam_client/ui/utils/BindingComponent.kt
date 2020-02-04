package com.chetdeva.spinnerbindings.binding

import androidx.databinding.DataBindingComponent


/**
 * Copyright (c) 2017 Fueled. All rights reserved.
 * @author chetansachdeva on 09/04/18
 */
class BindingComponent : DataBindingComponent {
    override fun getSpinnerBindings(): SpinnerBindings {
        return SpinnerBindings()
    }

    override fun getInverseSpinnerBindings(): InverseSpinnerBindings {
        return InverseSpinnerBindings()
    }
}