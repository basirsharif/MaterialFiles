/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.textfield.TextInputEditText
import me.zhanghai.android.files.R
import me.zhanghai.android.files.util.getColorStateListByAttr

class TextInputTextView : TextInputEditText {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        if (isTextSelectable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusable = View.FOCUSABLE_AUTO
            }
        } else {
            isClickable = false
            isFocusable = false
        }
    }

    override fun getFreezesText(): Boolean = false

    override fun getDefaultEditable(): Boolean = false

    override fun getDefaultMovementMethod(): MovementMethod? = null

    override fun setBackgroundDrawable(background: Drawable?) {
        var background = background
        if (!isTextSelectable && background is MaterialShapeDrawable) {
            background = addRippleEffect(background)
        }
        @Suppress("DEPRECATION")
        super.setBackgroundDrawable(background)
    }

    // @see com.google.android.material.textfield.DropdownMenuEndIconDelegate#addRippleEffect(
    //      AutoCompleteTextView)
    private fun addRippleEffect(boxBackground: MaterialShapeDrawable): Drawable {
        val rippleColor = context.getColorStateListByAttr(R.attr.colorControlHighlight)
        val mask = MaterialShapeDrawable(boxBackground.shapeAppearanceModel)
            .apply { setTint(Color.WHITE) }
        return RippleDrawable(rippleColor, boxBackground, mask)
    }
}
