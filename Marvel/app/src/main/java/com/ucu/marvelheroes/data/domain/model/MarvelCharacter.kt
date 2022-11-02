package com.ucu.marvelheroes.data.domain.model

data class MarvelCharacter(
    val id: String,
    val name: String,
    val description: String,
    val thumbnailUrl: String?
)