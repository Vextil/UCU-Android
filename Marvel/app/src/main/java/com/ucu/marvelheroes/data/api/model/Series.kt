package com.ucu.marvelheroes.data.api.model

data class Series(
    val available: String,
    val collectionURI: String,
    val items: List<Item>,
    val returned: String
)