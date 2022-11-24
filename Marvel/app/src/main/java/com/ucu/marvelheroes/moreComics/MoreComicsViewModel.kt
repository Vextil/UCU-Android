package com.ucu.marvelheroes.moreComics

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class MoreComicsViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {
    var offset = 0
    var loading = MutableLiveData(false)
    var search = MutableLiveData(0)

    private val _characters = MutableLiveData(listOf<MarvelComic>())
    val characters: LiveData<List<MarvelComic>>
        get() = _characters

    fun load(query: Int) {
        viewModelScope.launch {
            if (loading.value == true) return@launch
            loading.value = true
            search.value = query
            val characters = charactersRepository.fetchComics(query)
            _characters.value = characters
            loading.value = false
        }
    }

    fun loadMore(query: Int) {
        viewModelScope.launch {
            if (loading.value == true) return@launch
            loading.value = true
            offset += 10
            val characters = charactersRepository.fetchComics(query, offset)
            _characters.value = _characters.value?.plus(characters)
            loading.value = false
        }
    }

}