package com.example.bmehomework.utils

import android.graphics.drawable.Drawable
import com.example.bmehomework.R
import android.animation.Animator

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.view.View
import androidx.core.content.ContextCompat
import com.example.bmehomework.utils.AppUtil.format
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


object AppUtil {
    fun getCategoryByPrice(categoryPrice: String): Int {
        return when(categoryPrice){
            "FREE" -> R.drawable.ic_pull_up
            else -> R.drawable.ic_gym
        }
    }

    /**
     * @param view         View to animate
     * @param toVisibility Visibility at the end of animation
     * @param toAlpha      Alpha at the end of animation
     * @param duration     Animation duration in ms
     */
    fun animateView(view: View, toVisibility: Int, toAlpha: Float, duration: Int) {
        val show = toVisibility == View.VISIBLE
        if (show) {
            view.setAlpha(0F)
        }
        view.setVisibility(View.VISIBLE)
        view.animate()
            .setDuration(duration.toLong())
            .alpha((if (show) toAlpha else 0) as Float)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.setVisibility(toVisibility)
                }
            })
    }

    fun Float.format(fracDigits: Int): String {
        val df = DecimalFormat()
        df.setMaximumFractionDigits(fracDigits)
        return df.format(this)
    }

    fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d", hour, min, sec)

    fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    fun getMinutesFromTimestamp(ts: Long): Long {
        return TimeUnit.MILLISECONDS.toMinutes(ts)
    }

    fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun getDistanceBetweenTwoLocation(first: Location, end: Location): String{
        return ((first.distanceTo(
            end
        ) / 1000)).format(2) + " km"
    }
}