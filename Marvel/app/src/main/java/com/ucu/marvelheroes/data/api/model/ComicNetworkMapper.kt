package com.ucu.marvelheroes.data.api.model

import android.util.Log
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.domain.util.EntityMapper

object ComicNetworkMapper: EntityMapper<Result, MarvelComic> {

    override fun mapFromEntity(entity: Result): MarvelComic {
        Log.v("UNO",entity.toString())
        return MarvelComic(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            issueNumber = entity.issueNumber,
            thumbnailUrl = "${entity.thumbnail.path}.${entity.thumbnail.extension}",
            pageCount = entity.pageCount
        )
    }

    //Not needed for this sample
    override fun mapToEntity(domainModel: MarvelComic): Result {
        TODO("Not yet implemented")
    }

    fun fromGetCharactersResponse(networkResponse: CharactersResponse): List<MarvelComic> {
        return networkResponse.data.results.map { mapFromEntity(it) }
    }
}