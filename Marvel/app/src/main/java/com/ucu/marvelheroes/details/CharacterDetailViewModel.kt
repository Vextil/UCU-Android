package com.ucu.marvelheroes.details

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import com.ucu.marvelheroes.home.HomeViewModel
import kotlinx.coroutines.launch

class CharacterDetailViewModel: ViewModel() {

    private val charactersRepository = CharactersRepository

    private val _characters = MutableLiveData<List<MarvelComic>>()
    val characters: LiveData<List<MarvelComic>>
        get() = _characters

    fun refreshComics(query: Int){
            viewModelScope.launch {
                charactersRepository.fetchComics(query).run {
                    _characters.postValue(this)
                }
            }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CharacterDetailViewModel() as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}