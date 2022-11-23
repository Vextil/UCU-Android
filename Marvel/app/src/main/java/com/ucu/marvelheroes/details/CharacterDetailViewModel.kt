package com.ucu.marvelheroes.details

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import coil.load
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import kotlinx.coroutines.launch

class CharacterDetailViewModel(private val charactersRepository: CharactersRepository) :
    ViewModel() {

    var loading = MutableLiveData(false)
    val character = MutableLiveData<MarvelCharacter>()

    val comics = MutableLiveData<List<MarvelComic>>()


    fun loadComics() {
        viewModelScope.launch {
            val characterId = character.value?.id?.toInt()
            if (loading.value == true || characterId == null) return@launch
            loading.value = true
            comics.value = charactersRepository.fetchComics(characterId, 0)
            loading.value = false
        }
    }

    companion object {

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String?) {
            view.load(url ?: "")
        }
    }

}