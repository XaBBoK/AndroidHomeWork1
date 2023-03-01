package ru.netology.nmedia.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

fun ImageView.load(
    url: String,
    @DrawableRes placeholder: Int,
    timeout: Int = 10000
) {
    Glide.with(this)
        .load(url)
        .timeout(timeout)
        .placeholder(placeholder)
        /*.run {
            if (roundedCornersRadius > 0) {
                this.transform(RoundedCorners(roundedCornersRadius))
            } else this
        }*/
        .into(this)
}