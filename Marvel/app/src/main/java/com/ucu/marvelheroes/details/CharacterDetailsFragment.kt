package com.ucu.marvelheroes.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.comicdetails.ComicDetailsFragment
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.home.CharacterAdapter
import com.ucu.marvelheroes.moreComics.MoreComicsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterDetailsFragment : Fragment(), OnComicItemClickListener {
    private lateinit var characterId: String
    private lateinit var characterName: String
    private lateinit var characterDescription: String
    private lateinit var characterImageUrl: String
    private lateinit var adapter: ComicAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val viewModel: CharacterDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            characterId = it.getString("characterId") ?: ""
            characterName = it.getString("characterName") ?: ""
            characterDescription = it.getString("characterDescription") ?: ""
            characterImageUrl = it.getString("characterImage") ?: ""
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val characterName = requireView().findViewById<TextView>(R.id.characterName)
        val characterDescription = requireView().findViewById<TextView>(R.id.characterDescription)
        val characterImage = requireView().findViewById<ImageView>(R.id.characterImage)
        characterName.text = this.characterName
        if (this.characterDescription == "") {
            characterDescription.text = "No description"
        } else {
            characterDescription.text = this.characterDescription
        }

        characterImage.load(characterImageUrl)
        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.layoutManager = layoutManager
        adapter = ComicAdapter(viewModel.characters.value ?: emptyList(),this)

        recycler.adapter = adapter
        viewModel.load(characterId.toInt())
        viewModel.characters.observe(viewLifecycleOwner) {

            adapter.update(it)
            if (it.isEmpty()) {
                requireView().findViewById<View>(R.id.comicFrameMoreComics).visibility = View.GONE
                recycler.visibility = View.GONE
                requireView().findViewById<View>(R.id.emptyview).visibility = View.VISIBLE
            } else {
                requireView().findViewById<View>(R.id.comicFrameMoreComics).visibility = View.VISIBLE
                recycler.visibility = View.VISIBLE
                requireView().findViewById<View>(R.id.emptyview).visibility = View.GONE

            }

        }
        BottomSheetBehavior.from(view?.findViewById(R.id.bottom_sheet)!!).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val buttonMoreComics = requireView().findViewById<Button>(R.id.buttonMoreComics)
        buttonMoreComics.setOnClickListener {
            val fragment = MoreComicsFragment()
            val bundle = Bundle()
            bundle.putString("characterId", characterId)
            fragment.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.home_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

      override fun onPause() {
            super.onPause()
            viewModel.characters.removeObservers(this)
        }

    override fun onItemClick(item: MarvelComic, position: Int) {
        Log.v("LLLEVAR A DETALLE", item.issueNumber.toString())

        val detailsFragment = ComicDetailsFragment()
        detailsFragment.arguments = Bundle().apply {
           putParcelable(ComicDetailsFragment.ARG_COMIC, item)
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_layout, detailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CharacterDetailsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}