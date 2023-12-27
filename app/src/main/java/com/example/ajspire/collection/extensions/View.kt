package com.example.ajspire.collection.extensions

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.LinearInterpolator

fun View.startBlinkAnimation() {
    visibility=View.VISIBLE
    tag = getBlingAnimation(this)
    (tag as ObjectAnimator).start()
}

fun View.stopBlinkAnimation() {
    visibility = View.VISIBLE
    val valueAnimator = (tag as ObjectAnimator)

    valueAnimator.removeAllUpdateListeners()
    valueAnimator.removeAllListeners()
    valueAnimator.end()
    valueAnimator.cancel()
    tag = null
}

private fun getBlingAnimation(view: View): ObjectAnimator {
    val blinkanimation = ObjectAnimator.ofInt(
        view,
        "backgroundColor",
        Color.WHITE,
        Color.YELLOW
    ) // Change alpha from fully visible to invisible
    blinkanimation.duration = 800 // duration
    blinkanimation.setEvaluator(ArgbEvaluator())
    blinkanimation.interpolator = LinearInterpolator() // do not alter animation rate
    blinkanimation.repeatCount = -1 // Repeat animation infinitely
    blinkanimation.repeatMode = ValueAnimator.RESTART
    return blinkanimation
}
