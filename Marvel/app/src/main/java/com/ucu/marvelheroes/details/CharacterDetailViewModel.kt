package com.ucu.marvelheroes.details

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class CharacterDetailViewModel(private val charactersRepository: CharactersRepository): ViewModel() {
    var loading = MutableLiveData(false)
    var search = MutableLiveData(0)
    var offset = 0
    private val _characters = MutableLiveData<List<MarvelComic>>()
    val characters: LiveData<List<MarvelComic>>
        get() = _characters


    fun load(query: Int) {
        viewModelScope.launch {
            if (loading.value == true) return@launch
            loading.value = true
            val characters = charactersRepository.fetchComics(query, 0)
            _characters.value = characters
            loading.value = false
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            if (loading.value == true) return@launch
            loading.value = true
            offset += 20
            val characters = search.value?.let { charactersRepository.fetchComics(it, offset) }
            _characters.value = _characters.value?.plus(characters!!)
            loading.value = false
        }
    }

    fun refreshComics(query: Int){
            viewModelScope.launch {
                charactersRepository.fetchComics(query).run {
                    _characters.postValue(this)
                }
            }
    }



}