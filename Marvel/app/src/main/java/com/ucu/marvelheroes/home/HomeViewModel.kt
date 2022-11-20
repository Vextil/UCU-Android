package com.ucu.marvelheroes.home

import android.util.Log
import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val charactersRepository = CharactersRepository
    var offset = 0
    var loading = false

    private val _characters = MutableLiveData<ArrayList<MarvelCharacter>>()
    val characters: LiveData<ArrayList<MarvelCharacter>>
        get() = _characters

    fun newCharacters(query: String) {
        if (query.isEmpty()) {
            viewModelScope.launch {
                charactersRepository.fetchCharacters(offset).run {
                    if (_characters.value == null) {
                        _characters.value = this as ArrayList<MarvelCharacter>
                    } else {
                        _characters.postValue(this as ArrayList<MarvelCharacter>?)
                    }
                    loading = false
                    Log.v("HomeViewModel", "refreshCharacters: ${_characters.value?.size}")
                }
            }
        } else {
            viewModelScope.launch {
                Log.v("HomeViewModel", "refreshCharacters: $query")
                charactersRepository.fetchCharactersStartsWith(query, offset).run {
                    _characters.postValue(this as ArrayList<MarvelCharacter>?)
                    Log.v("HomeViewModel", "refreshCharacters: ${_characters.value?.size}")
                    loading = false
                }
            }
        }
    }

    private fun moreCharacters(query: String){
        if (query.isEmpty()) {
            viewModelScope.launch {
                charactersRepository.fetchCharacters(offset).run {
                    if (_characters.value == null) {
                        _characters.value = this as ArrayList<MarvelCharacter>
                    }else {
                        _characters.value?.addAll(this ?: emptyList())
                    }
                    loading = false
                    Log.v("HomeViewModel", "refreshCharacters: ${_characters.value?.size}")
                }
            }
        } else {
            viewModelScope.launch {
                Log.v("HomeViewModel", "refreshCharacters: $query")
                charactersRepository.fetchCharactersStartsWith(query,offset).run {
                    _characters.value?.addAll(this ?: emptyList())
                    Log.v("HomeViewModel", "refreshCharacters: ${_characters.value?.size}")
                    loading = false
                }
            }
        }
    }

    fun loadMore(query: String) {
        loading = true
        offset += 20
        moreCharacters(query)

    }

    fun searchCharacters(query: String){
        loading = true
        offset = 0
        Log.v("HomeViewModel", "searchCharacters: ${_characters.value}")
        newCharacters(query)
        Log.v("HomeViewModel", "searchCharacters: ${_characters.value?.size}")
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