package com.ucu.marvelheroes.data.domain.model

data class MarvelCharacter(
    val id: String,
    val name: String,
    val description: String,
    val thumbnailUrl: String?
)

data class MarvelComic(
    val id: String,
    val title: String,
    val description: String?,
    val issueNumber: Double,
    val thumbnailUrl: String?
)