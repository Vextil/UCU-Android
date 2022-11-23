package com.ucu.marvelheroes.comicDetails

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import coil.load
import com.ucu.marvelheroes.data.domain.model.MarvelComic

class ComicDetailViewModel() : ViewModel() {

    val comic: MutableLiveData<MarvelComic> = MutableLiveData(null)

    companion object {

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String?) {
            view.load(url ?: "")
        }
    }
}
