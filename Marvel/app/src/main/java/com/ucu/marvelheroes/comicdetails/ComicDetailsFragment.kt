package com.ucu.marvelheroes.comicdetails

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
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.details.ComicAdapter

class ComicDetailsFragment : Fragment() {
    private lateinit var comicId: String
    private lateinit var comicName: String
    private lateinit var comicDescription: String
    private lateinit var comicImageUrl: String
    private var comicIssueNumber: Double = 0.0

    private val viewModel: ComicDetailViewModel by lazy {
        ViewModelProvider(this, ComicDetailViewModel.Factory())[ComicDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Log.v("ComicDetailsFragment", "onCreate")
            comicId = it.getString("comicId") ?: ""
            Log.v("ComicDetailsFragment", "comicId: $comicId")
            comicName = it.getString("comicName") ?: ""
            Log.v("ComicDetailsFragment", "comicName: $comicName")
            comicDescription = it.getString("comicDescription") ?: ""
            Log.v("ComicDetailsFragment", "comicDescription: $comicDescription")
            comicImageUrl = it.getString("comicImage") ?: ""
            Log.v("ComicDetailsFragment", "comicImageUrl: $comicImageUrl")
            comicIssueNumber = it.getDouble("comicIssueNumber")
            Log.v("ComicDetailsFragment", "comicIssueNumber: $comicIssueNumber")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_comicdetails, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onResume() {
        super.onResume()
//        BottomSheetBehavior.from(view?.findViewById(R.id.bottom_sheet)!!).apply {
//            peekHeight = 100
//            this.state = BottomSheetBehavior.STATE_COLLAPSED
//        }

//        viewModel.characters.observe(this)
//        {
//            val recycler = requireView().findViewById<RecyclerView>(R.id.recycler)
//            recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//
//            recycler.adapter = ComicAdapter(viewModel.characters.value ?: emptyList(),this)
//        }
        val comicName = requireView().findViewById<TextView>(R.id.comicName)
        val comicDescription = requireView().findViewById<TextView>(R.id.comicDescription)
        val comicImage = requireView().findViewById<ImageView>(R.id.comicImage)
        val comicIssueNumber = requireView().findViewById<TextView>(R.id.comicIssueNumber)
        comicName.text = this.comicName
        comicIssueNumber.text = "Issue: " + this.comicIssueNumber.toString()
        if (this.comicDescription == "") {
            comicDescription.text = "No description"
        } else {
            comicDescription.text = this.comicDescription
        }
        val url = comicImageUrl.replace("http", "https")

        comicImage.load(url)

    }
    override fun onPause() {
        super.onPause()
        viewModel.characters.removeObservers(this)
    }


}