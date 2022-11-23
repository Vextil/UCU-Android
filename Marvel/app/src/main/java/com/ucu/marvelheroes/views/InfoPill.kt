package com.ucu.marvelheroes.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ucu.marvelheroes.R

class InfoPill(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    val pages: TextView by lazy { findViewById(R.id.pages) }
    val issueNumber: TextView by lazy { findViewById(R.id.issue_number) }

    init {
        inflate(context, R.layout.view_info_pill, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InfoPill,
            0, 0
        ).apply {

            try {
                pages.text = getText(R.styleable.InfoPill_pages)
                issueNumber.text = getText(R.styleable.InfoPill_issueNumber)

            } finally {
                recycle()
            }
        }
    }

    fun setPages(pages: String) {
        this.pages.text = pages
    }

    fun setIssueNumber(issueNumber: String) {
        this.issueNumber.text = issueNumber
    }

    companion object {
        @BindingAdapter("app:pages")
        @JvmStatic
        fun setCustomPages(view: InfoPill, pagesAmount: Int) {
            view.setPages(pagesAmount.toString())
        }

        @BindingAdapter("app:issueNumber")
        @JvmStatic
        fun setCustomIssueNumber(view: InfoPill, issue: Double) {
            view.setIssueNumber(issue.toString())
        }
    }

}