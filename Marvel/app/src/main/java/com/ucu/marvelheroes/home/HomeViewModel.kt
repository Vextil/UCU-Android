package com.ucu.marvelheroes.home

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    var offset = 0
    var loading = MutableLiveData(false)
    var search = MutableLiveData("")

    private val _characters = MutableLiveData(listOf<MarvelCharacter>())
    val characters: LiveData<List<MarvelCharacter>>
        get() = _characters

    fun load(query: String?) {
        viewModelScope.launch {
            loading.value = true
            search.value = query
            val characters = charactersRepository.fetchCharacters(search.value, 0)
            _characters.value = characters
            loading.value = false
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            if (loading.value != true) {
                loading.value = true
                offset += 20
                charactersRepository.fetchCharacters(search.value, offset).run {
                    _characters.value = _characters.value?.plus(this)
                    loading.value = false
                }
            }
        }
    }
}