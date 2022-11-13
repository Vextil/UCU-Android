package com.ucu.marvelheroes.comicdetails

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch


    class ComicDetailViewModel: ViewModel() {


        private val _characters = MutableLiveData<List<MarvelComic>>()
        val characters: LiveData<List<MarvelComic>>
            get() = _characters


        class Factory : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ComicDetailViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ComicDetailViewModel() as T
                }
                throw IllegalArgumentException("Unable to construct viewmodel")
            }
        }
    }
