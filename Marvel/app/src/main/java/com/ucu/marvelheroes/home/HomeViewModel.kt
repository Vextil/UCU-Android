package com.ucu.marvelheroes.home

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    var offset = 0
    val loading = MutableLiveData(false)
    val notFound = MutableLiveData(false)
    val search = MutableLiveData("")

    private val _characters = MutableLiveData(listOf<MarvelCharacter>())
    val characters: LiveData<List<MarvelCharacter>>
        get() = _characters

    fun load(query: String?) {
        viewModelScope.launch {
            if (loading.value == true) return@launch
            notFound.value = false
            loading.value = true
            search.value = query
            val characters = charactersRepository.fetchCharacters(search.value, 0)
            _characters.value = characters
            if (characters.isEmpty()) {
                notFound.value = true
            }
            loading.value = false
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            if (loading.value == true) return@launch
            loading.value = true
            offset += 20
            val characters = charactersRepository.fetchCharacters(search.value, offset)
            _characters.value = _characters.value?.plus(characters)
            loading.value = false
        }
    }
}