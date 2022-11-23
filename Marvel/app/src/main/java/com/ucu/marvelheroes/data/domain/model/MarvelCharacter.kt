package com.ucu.marvelheroes.data.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class MarvelCharacter(
    val id: String,
    val name: String,
    val description: String,
    val thumbnailUrl: String?,
    var height: Float = Random.nextFloat() * 100 + 150,
): Parcelable