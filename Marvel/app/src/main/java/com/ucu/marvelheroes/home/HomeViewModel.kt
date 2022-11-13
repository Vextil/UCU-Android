package com.ucu.marvelheroes.home

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val charactersRepository = CharactersRepository
    var offset = 0

    private val _characters = MutableLiveData<List<MarvelCharacter>>()
    val characters: LiveData<List<MarvelCharacter>>
        get() = _characters

    fun refreshCharacters(query: String){
        if (query.isEmpty()) {
            viewModelScope.launch {
                charactersRepository.fetchCharacters().run {
                    _characters.postValue(this)
                }
            }
        } else {
            viewModelScope.launch {
                charactersRepository.fetchCharacters(query).run {
                    _characters.postValue(this)
                }
            }
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel() as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}