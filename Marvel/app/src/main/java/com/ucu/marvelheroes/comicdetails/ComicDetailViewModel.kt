package com.ucu.marvelheroes.comicdetails

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelComic

class ComicDetailViewModel() : ViewModel() {

    private val _characters = MutableLiveData<List<MarvelComic>>()
    val characters: LiveData<List<MarvelComic>>
        get() = _characters

}
