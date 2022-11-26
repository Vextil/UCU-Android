package com.ucu.marvelheroes.home

import android.view.View
import androidx.lifecycle.*
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(
    private val charactersRepository: CharactersRepository
) : ViewModel() {

    var offset = 0
    var loadingMore = false
    val loading = MutableLiveData(false)
    val notFound = MutableLiveData(false)
    val showShimmer = MutableLiveData(true)
    val search = MutableLiveData("")
    val characters: MutableLiveData<HomeCharactersState> = MutableLiveData()

    private var timer: Timer = Timer()


    fun load(query: String?) {
        viewModelScope.launch {
            notFound.value = false
            loading.value = true
            showShimmer.value = true
            offset = 0
            search.value = query
            val newChars = charactersRepository.fetchCharacters(search.value, 0)
            characters.value = HomeCharactersState(
                newItems = newChars,
                allItems = newChars,
                newSearch = true
            )
            if (newChars.isEmpty()) {
                notFound.value = true
                showShimmer.value = false
            } else {
                showShimmer.value = true
            }
            loading.value = false
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            if (loadingMore) return@launch
            loadingMore = true
            loading.value = true
            offset += 20
            val newChars = charactersRepository.fetchCharacters(search.value, offset)
            if (newChars.isNotEmpty()) {
                characters.value = HomeCharactersState(
                    newItems = newChars,
                    allItems = characters.value?.allItems?.plus(newChars) ?: newChars,
                    newSearch = false
                )
            }
            showShimmer.value = newChars.isNotEmpty()
            loading.value = false
            loadingMore = false
        }
    }

    fun clearSearch() {
        characters.value = HomeCharactersState(listOf(), listOf(), true)
        search.value = ""
        showShimmer.value = true
    }

    fun onSearchChanged(text: CharSequence) {
        timer.cancel()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                load(text.toString())
            }
        }, 500)
    }

    data class HomeCharactersState(
        val newItems: List<MarvelCharacter>,
        val allItems: List<MarvelCharacter>,
        val newSearch: Boolean
    )
}