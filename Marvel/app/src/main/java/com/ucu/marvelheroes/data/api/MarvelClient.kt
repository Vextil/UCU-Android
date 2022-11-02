package com.ucu.marvelheroes.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MarvelClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://gateway.marvel.com:443/v1/public/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(ApiService::class.java)
}