package com.ucu.marvelheroes.details

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class CharacterDetailViewModel(private val charactersRepository: CharactersRepository): ViewModel() {

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

}