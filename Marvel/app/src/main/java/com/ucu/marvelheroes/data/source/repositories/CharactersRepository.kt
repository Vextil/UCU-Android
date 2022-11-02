package com.ucu.marvelheroes.data.source.repositories

import com.ucu.marvelheroes.BuildConfig
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.api.MarvelClient
import com.ucu.marvelheroes.data.api.model.CharacterNetworkMapper
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
}