package com.ucu.marvelheroes.comicDetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.databinding.FragmentComicdetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComicDetailsFragment : Fragment() {

    companion object {
        const val ARG_COMIC = "comic"
    }

    private val viewModel: ComicDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.comic.value = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelable(ARG_COMIC, MarvelComic::class.java)
            } else {
                it.getParcelable(ARG_COMIC)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentComicdetailsBinding>(
            inflater,
            R.layout.fragment_comicdetails,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

}