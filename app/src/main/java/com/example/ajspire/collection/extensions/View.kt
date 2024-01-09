package com.example.ajspire.collection.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import com.example.ajspire.collection.R


fun View.startBlinkAnimation() {
    visibility = View.VISIBLE
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

fun View.setBitmapBackground(bitmap: Bitmap) {
    val bd = BitmapDrawable(this.context.resources, bitmap)
    this.background = bd
}

fun View.fadeAnimation() {
    val fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, .3f)
    fadeOut.duration = 2000
    val fadeIn = ObjectAnimator.ofFloat(this, "alpha", .3f, 1f)
    fadeIn.duration = 2000

    val mAnimationSet = AnimatorSet()

    mAnimationSet.play(fadeIn).after(fadeOut)
    mAnimationSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            mAnimationSet.start()
        }
    })
    mAnimationSet.start()
}

fun View.slideDownAnimation() {
    val animSlideDown: Animation =
        AnimationUtils.loadAnimation(this.context.applicationContext, R.anim.slide_down)
    this.startAnimation(animSlideDown)
}
fun View.slideUpAnimation() {
    val animSlideDown: Animation =
        AnimationUtils.loadAnimation(this.context.applicationContext, R.anim.slide_up)
    this.startAnimation(animSlideDown)
}
