package com.ucu.marvelheroes.moreComics

import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class MoreComicsViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {
    var offset = 0
    val loading = MutableLiveData(false)
    var showShimmer = MutableLiveData<Boolean>(true)
    var loadingMore = false

    private val _comics = MutableLiveData(listOf<MarvelComic>())
    val comics: LiveData<List<MarvelComic>>
        get() = _comics

    fun load(query: Int) {
        viewModelScope.launch {
            if (loading.value == true) return@launch
            loading.value = true
            showShimmer.value = true
            val comics = charactersRepository.fetchComics(query)
            _comics.value = comics
            showShimmer.value = comics.isNotEmpty()
            loading.value = false
        }
    }

    fun loadMore(query: Int) {
        viewModelScope.launch {
            if (loadingMore) return@launch
            loadingMore = true
            loading.value = true
            offset += 10
            val comics = charactersRepository.fetchComics(query, offset)
            showShimmer.value = comics.isNotEmpty()
            _comics.value = _comics.value?.plus(comics)
            loading.value = false
            loadingMore = false
        }
    }

}