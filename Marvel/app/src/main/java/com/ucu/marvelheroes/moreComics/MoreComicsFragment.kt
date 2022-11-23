package com.ucu.marvelheroes.moreComics

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.details.CharacterDetailsFragment
import com.ucu.marvelheroes.home.HomeFragment
import com.ucu.marvelheroes.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MoreComicsFragment : Fragment(), onComicItemClickListener {
    private lateinit var characterId: String
    private var timer = Timer()
    private lateinit var adapter: MoreComicsAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val viewModel: MoreComicsViewModel by viewModel()

    companion object {
        const val ARG_CHARACTER_ID = "characterId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            characterId = it.getString(ARG_CHARACTER_ID) ?: ""
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_viewcomics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("MoreComicsFragment", "characterId: $characterId")
        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        recycler.setHasFixedSize(true)
        recycler.layoutManager = layoutManager
        adapter = MoreComicsAdapter(listOf(), this)
        recycler.adapter = adapter
        recycler.addOnScrollListener(MoreComicsFragment.OnScrollListener(viewModel, layoutManager,characterId))
        viewModel.load(characterId.toInt())
        Log.v("MoreComicsFragment", viewModel.characters.value.toString())
        viewModel.characters.observe(viewLifecycleOwner) {
            adapter.update(it)
        }

    }
    class OnScrollListener(
        val viewModel: MoreComicsViewModel,
        private val layoutManager: StaggeredGridLayoutManager,
        val characterId : String
    ) : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPositions = layoutManager.findFirstVisibleItemPositions(null)
            var firstVisibleItemPosition = 0
            if (firstVisibleItemPositions.size > 0) {
                firstVisibleItemPosition = firstVisibleItemPositions[0]
            }
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                && firstVisibleItemPosition >= 0
                && totalItemCount >= 10
            ) {
                Log.v("...", " Reached Last Item")
                viewModel.loadMore(characterId.toInt())
            }
       }
    }
    override fun onComicItemClick(item: MarvelComic, position: Int) {
//        val detailsFragment = CharacterDetailsFragment()
//        val bundle = Bundle()
////        bundle.putString("characterId", item.id)
////        bundle.putString("characterName", item.title)
////        bundle.putString("characterDescription", item.description)
////        bundle.putString("characterImage", item.thumbnailUrl)
//        detailsFragment.arguments = bundle
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.home_layout, detailsFragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
    }

}