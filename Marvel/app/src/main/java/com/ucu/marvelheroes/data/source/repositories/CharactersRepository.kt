package com.ucu.marvelheroes.data.source.repositories

import android.util.Log
import com.ucu.marvelheroes.BuildConfig
import com.ucu.marvelheroes.data.api.ApiService
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.api.model.CharacterNetworkMapper
import com.ucu.marvelheroes.data.api.model.ComicNetworkMapper
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.interfaces.ICharactersRepository
import com.ucu.marvelheroes.extensions.md5
import com.ucu.marvelheroes.extensions.toHex
import java.util.*

class CharactersRepository(private val marvelService: ApiService) : ICharactersRepository {

    override suspend fun fetchCharacters(query: String?, offset: Int): List<MarvelCharacter> {
        val timeStamp = Date().time.toString()
        val characters = if (query.isNullOrEmpty()) {
            marvelService.listCharacters(
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "name",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5()
                    .toHex(),
                offset = offset
            )
        } else {
            marvelService.listCharacters(
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "name",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5()
                    .toHex(),
                nameStartsWith = query,
                offset = offset
            )
        }

        return CharacterNetworkMapper.fromGetCharactersResponse(characters)
    }

    override suspend fun fetchComics(characterId: Int): List<MarvelComic> {
        val timeStamp = Date().time.toString()
        val comics = marvelService
            .getComics(
                characterId = characterId,
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "title",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5()
                    .toHex(),
                limit = 10
            )
        Log.v("comics", comics.toString())
        return ComicNetworkMapper.fromGetCharactersResponse(comics)
    }

    override suspend fun fetchComics(characterId: Int,offset: Int): List<MarvelComic> {
        val timeStamp = Date().time.toString()
        val comics = marvelService
            .getComics(
                characterId = characterId,
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "title",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5()
                    .toHex(),
                offset = offset
            )
        Log.v("comics", comics.toString())
        return ComicNetworkMapper.fromGetCharactersResponse(comics)
    }
}