package com.ucu.marvelheroes.data.source.interfaces

import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic

interface ICharactersRepository {
    suspend fun fetchCharacters(query: String?, offset: Int): List<MarvelCharacter>
    suspend fun fetchComics(characterId: Int): List<MarvelComic>
}