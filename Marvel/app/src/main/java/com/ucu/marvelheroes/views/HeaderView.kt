package com.ucu.marvelheroes.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.extensions.getActivity

class HeaderView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    val text: TextView by lazy { findViewById(R.id.headerText) }
    val backButton: ImageView by lazy { findViewById(R.id.backButton) }

    init {
        inflate(context, R.layout.view_header, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HeaderView,
            0, 0
        ).apply {

            try {
                text.text = getText(R.styleable.HeaderView_text)
            } finally {
                recycle()
            }
        }

        backButton.setOnClickListener {
            context.getActivity()?.onBackPressed()
        }

    }
}
