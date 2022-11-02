package com.ucu.marvelheroes.data.api

import com.ucu.marvelheroes.data.api.model.CharactersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("characters")
    suspend fun listCharacters(
        @Query("apikey") apiKey: String,
        @Query("orderBy") orderBy: String,
        @Query("ts") ts: String,
        @Query("hash") hash: String,
    ): CharactersResponse

}