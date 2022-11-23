package com.ucu.marvelheroes.details

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.comicDetails.ComicDetailsFragment
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.databinding.FragmentDetailsBinding
import com.ucu.marvelheroes.moreComics.MoreComicsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterDetailsFragment : Fragment(), OnComicItemClickListener {

    private lateinit var adapter: ComicAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val viewModel: CharacterDetailViewModel by viewModel()

    private val frameMoreComics: FrameLayout
        get() = requireView().findViewById(R.id.comicFrameMoreComics)
    private val buttonMoreComics: Button
        get() = requireView().findViewById(R.id.buttonMoreComics)
    private val noComicsView: TextView
        get() = requireView().findViewById(R.id.emptyview)
    private val recycler: RecyclerView
        get() = requireView().findViewById(R.id.recycler)
    private val bottomSheet: RelativeLayout
        get() = requireView().findViewById(R.id.bottom_sheet)

    companion object {
        const val ARG_CHARACTER = "character"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.character.value = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelable(ARG_CHARACTER, MarvelCharacter::class.java)
            } else {
                it.getParcelable(ARG_CHARACTER)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentDetailsBinding>(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.layoutManager = layoutManager
        adapter = ComicAdapter(viewModel.comics.value ?: emptyList(), this)
        recycler.adapter = adapter
        if (viewModel.comics.value.isNullOrEmpty()) {
            viewModel.loadComics()
        }
        viewModel.comics.observe(viewLifecycleOwner) {
            adapter.update(it)
            if (it.isEmpty()) {
                frameMoreComics.visibility = View.GONE
                recycler.visibility = View.GONE
                noComicsView.visibility = View.VISIBLE
            } else {
                frameMoreComics.visibility = View.VISIBLE
                recycler.visibility = View.VISIBLE
                noComicsView.visibility = View.GONE
            }
        }
        BottomSheetBehavior.from(bottomSheet).apply {
            val r: Resources = view.context.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50f,
                r.displayMetrics
            )
            peekHeight = px.toInt()
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        buttonMoreComics.setOnClickListener {
            val fragment = MoreComicsFragment().apply {
                arguments = Bundle().apply {
                    putString(MoreComicsFragment.ARG_CHARACTER_ID, viewModel.character.value?.id)
                }
            }
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.home_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onItemClick(item: MarvelComic, position: Int) {
        val detailsFragment = ComicDetailsFragment()
        detailsFragment.arguments = Bundle().apply {
            putParcelable(ComicDetailsFragment.ARG_COMIC, item)
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_layout, detailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}