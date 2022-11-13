package com.ucu.marvelheroes.data.source.repositories

import android.util.Log
import com.ucu.marvelheroes.BuildConfig
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.api.MarvelClient
import com.ucu.marvelheroes.data.api.model.CharacterNetworkMapper
import com.ucu.marvelheroes.data.api.model.ComicNetworkMapper
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.extensions.md5
import com.ucu.marvelheroes.extensions.toHex
import java.util.*

object CharactersRepository {

    suspend fun fetchCharacters(): List<MarvelCharacter> {
        val timeStamp = Date().time.toString()
        val characters = MarvelClient.service
            .listCharacters(
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "-modified",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5().toHex()
            )

        return CharacterNetworkMapper.fromGetCharactersResponse(characters)
    }

    suspend fun fetchCharacters(offset: Int): List<MarvelCharacter> {
        val timeStamp = Date().time.toString()
        val characters = MarvelClient.service
            .listCharacters(
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "-modified",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5().toHex(),
                offset = offset
            )

        return CharacterNetworkMapper.fromGetCharactersResponse(characters)
    }

    suspend fun fetchCharacters(query: String): List<MarvelCharacter> {
        val timeStamp = Date().time.toString()
        val characters = MarvelClient.service
            .listCharacters(
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "-modified",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5().toHex(),
                nameStartsWith = query
            )

        return CharacterNetworkMapper.fromGetCharactersResponse(characters)
    }

    suspend fun fetchComics(characterId: Int): List<MarvelComic> {
        val timeStamp = Date().time.toString()
        val comics = MarvelClient.service
            .getComics(
                characterId = characterId,
                apiKey = BuildConfig.PUBLIC_KEY,
                orderBy = "-modified",
                ts = timeStamp,
                hash = "$timeStamp${BuildConfig.PRIVATE_KEY}${BuildConfig.PUBLIC_KEY}".md5().toHex()
            )
        Log.v("comics", comics.toString())
        return ComicNetworkMapper.fromGetCharactersResponse(comics)
    }
}