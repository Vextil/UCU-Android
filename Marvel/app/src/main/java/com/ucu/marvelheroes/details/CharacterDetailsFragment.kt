package com.ucu.marvelheroes.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.comicdetails.ComicDetailsFragment
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.home.CharacterAdapter

class CharacterDetailsFragment : Fragment(), onComicItemClickListener {
    private lateinit var characterId: String
    private lateinit var characterName: String
    private lateinit var characterDescription: String
    private lateinit var characterImageUrl: String
    private val viewModel: CharacterDetailViewModel by lazy {
        ViewModelProvider(this, CharacterDetailViewModel.Factory())[CharacterDetailViewModel::class.java]
    }

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
    }
    override fun onResume() {
        super.onResume()
        BottomSheetBehavior.from(view?.findViewById(R.id.bottom_sheet)!!).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.characters.observe(this)
        {
            val recycler = requireView().findViewById<RecyclerView>(R.id.recycler)
            recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            recycler.adapter = ComicAdapter(viewModel.characters.value ?: emptyList(),this)
        }
        Log.v("CharacterDetail", "characterId: $characterId")
        val characterName = requireView().findViewById<TextView>(R.id.characterName)
        val characterDescription = requireView().findViewById<TextView>(R.id.characterDescription)
        val characterImage = requireView().findViewById<ImageView>(R.id.characterImage)
        characterName.text = this.characterName
        if (this.characterDescription == "") {
            characterDescription.text = "No description"
        } else {
            characterDescription.text = this.characterDescription
        }

        val url = this.characterImageUrl?.substringBeforeLast("jpg")
        characterImage.load("$url.jpg")
        viewModel.refreshComics(characterId.toInt())
    }
      override fun onPause() {
            super.onPause()
            viewModel.characters.removeObservers(this)
        }

    override fun onItemClick(item: MarvelComic, position: Int) {
        Log.v("LLLEVAR A DETALLE", item.issueNumber.toString())

//        open detail fragment
        val detailsFragment = ComicDetailsFragment()
        val bundle = Bundle()

        bundle.putString("comicId", item.id)
        bundle.putString("comicName", item.title)
        bundle.putDouble("comicIssueNumber", item.issueNumber)
        bundle.putString("comicDescription", item.description)

        bundle.putString("comicImage", item.thumbnailUrl)
        detailsFragment.arguments = bundle
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