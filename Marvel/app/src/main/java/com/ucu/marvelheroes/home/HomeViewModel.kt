package com.ucu.marvelheroes.home

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.*
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.source.repositories.AuthRepository
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
    val search = MutableLiveData("")
    val popupVisible = MutableLiveData(false)

    private var timer: Timer = Timer()

    private val _characters = MutableLiveData(mutableListOf<MarvelCharacter>())
    val characters: LiveData<MutableList<MarvelCharacter>>
        get() = _characters

    fun load(query: String?) {
        viewModelScope.launch {
            notFound.value = false
            loading.value = true
            search.value = query
            val characters = charactersRepository.fetchCharacters(search.value, 0)
            _characters.value = characters.toMutableList()
            if (characters.isEmpty()) {
                notFound.value = true
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
            val characters = charactersRepository.fetchCharacters(search.value, offset)
            _characters.value?.addAll(characters)
            _characters.value = _characters.value
            loading.value = false
            loadingMore = false
        }
    }

    fun clearSearch() {
        search.value = ""
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

    fun showPopup(view: View) {
        popupVisible.value = true
    }
}