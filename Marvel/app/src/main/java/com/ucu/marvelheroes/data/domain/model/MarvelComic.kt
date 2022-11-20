package com.ucu.marvelheroes.data.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarvelComic(
    val id: String,
    val title: String,
    val description: String?,
    val issueNumber: Double,
    val thumbnailUrl: String?
): Parcelable