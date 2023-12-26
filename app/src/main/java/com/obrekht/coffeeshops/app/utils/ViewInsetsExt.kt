package com.obrekht.coffeeshops.app.utils

import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.google.android.material.math.MathUtils

fun View.setOnApplyWindowInsetsListener(block: (View, WindowInsetsCompat, Rect, Rect) -> WindowInsetsCompat) {
    val initialPadding = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
    val initialMargin = Rect(marginLeft, marginTop, marginRight, marginBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding, initialMargin)
    }

    doOnAttach {
        requestApplyInsets()
    }
}

object InsetsAnimationTranslationModifier {
    val Bottom = { value: Float -> value }
    val Centered = { value: Float -> value / 2 }
}

fun View.setKeyboardInsetsAnimationCallback(
    modifier: (value: Float) -> Float = InsetsAnimationTranslationModifier.Bottom
) {
    val view = this

    ViewCompat.setWindowInsetsAnimationCallback(
        this,
        object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
            var startBottom = 0f
            var endBottom = 0f

            override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                startBottom = modifier(view.bottom.toFloat())
            }

            override fun onStart(
                animation: WindowInsetsAnimationCompat,
                bounds: WindowInsetsAnimationCompat.BoundsCompat
            ): WindowInsetsAnimationCompat.BoundsCompat {
                endBottom = modifier(view.bottom.toFloat())

                view.translationY = startBottom - endBottom

                return bounds
            }

            override fun onProgress(
                insets: WindowInsetsCompat,
                runningAnimations: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat {
                val imeAnimation = runningAnimations.find {
                    it.typeMask and WindowInsetsCompat.Type.ime() != 0
                } ?: return insets

                view.translationY = MathUtils.lerp(
                    startBottom - endBottom,
                    0f,
                    imeAnimation.interpolatedFraction
                )

                return insets
            }
        }
    )
}
