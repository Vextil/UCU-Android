package com.ucu.marvelheroes.home

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
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.details.CharacterDetailsFragment
import java.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(), OnCharacterItemClickListener {

    private var timer = Timer()
    private lateinit var adapter: CharacterAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val viewModel: HomeViewModel by viewModel()

    private var previousSearch = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        recycler.setHasFixedSize(true)
        recycler.layoutManager = layoutManager
        adapter = CharacterAdapter(viewModel.characters.value ?: emptyList(), this)
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recycler.adapter = adapter
        recycler.addOnScrollListener(OnScrollListener(viewModel, layoutManager))
        if (viewModel.characters.value.isNullOrEmpty()) {
            viewModel.load(null)
        }
        viewModel.characters.observe(viewLifecycleOwner) {
            adapter.update(it)
            if (it.isEmpty()) {
                recycler.visibility = View.GONE
                requireView().findViewById<View>(R.id.emptyview).visibility = View.VISIBLE
            } else {
                recycler.visibility = View.VISIBLE
                requireView().findViewById<View>(R.id.emptyview).visibility = View.GONE
            }

        }
        setSearchTextChangeListener()

    }

    private fun setSearchTextChangeListener() {
        val charactersearch = requireView().findViewById<EditText>(R.id.charactersearch)
        charactersearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                timer.cancel()
                if (s.toString() != previousSearch) {
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            viewModel.load(s.toString())
                        }
                    }, 500)
                    previousSearch = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onItemClick(item: MarvelCharacter, position: Int) {
        val detailsFragment = CharacterDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(CharacterDetailsFragment.ARG_CHARACTER, item)
            }
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_layout, detailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    class OnScrollListener(
        val viewModel: HomeViewModel,
        private val layoutManager: StaggeredGridLayoutManager
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
                && totalItemCount >= 20
            ) {
                Log.v("...", " Reached Last Item")
                viewModel.loadMore()
            }
        }
    }


}