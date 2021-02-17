package com.inhelp.extension

import android.content.res.Resources
import android.util.TypedValue

val Int.dp: Float
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
    }

val Int.sp: Float
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics)
    }