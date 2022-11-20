package com.ucu.marvelheroes.data.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarvelCharacter(
    val id: String,
    val name: String,
    val description: String,
    val thumbnailUrl: String?
): Parcelable