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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.details.CharacterDetailsFragment
import java.util.*

class HomeFragment : Fragment(),onCharacterItemClickListener {
    private var timer = Timer()
    private lateinit var adapter: CharacterAdapter

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this, HomeViewModel.Factory())[HomeViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    override fun onResume() {
        super.onResume()
        setSearchTextChangeListener()
        viewModel.characters.observe(this)
        {
            val recycler = requireView().findViewById<RecyclerView>(R.id.recycler)
            recycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            recycler.adapter = CharacterAdapter(viewModel.characters.value ?: emptyList(),this)
        }
                viewModel.refreshCharacters("")
    }



    override fun onPause() {
        super.onPause()
        viewModel.characters.removeObservers(this)
    }

    private fun setSearchTextChangeListener(){
        val charactersearch = requireView().findViewById<EditText>(R.id.charactersearch)
        charactersearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        viewModel.refreshCharacters(charactersearch.text.toString())
                    }
                }, 500)


            }
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }




    override fun onItemClick(item: MarvelCharacter, position: Int) {
        Log.v("LLLEVAR A DETALLE", "LLEVAR A DETALLE")
//        open detail fragment

        val detailsFragment = CharacterDetailsFragment()
        val bundle = Bundle()
        bundle.putString("characterId", item.id)
        bundle.putString("characterName", item.name)
        bundle.putString("characterDescription", item.description)
        bundle.putString("characterImage", item.thumbnailUrl)
        detailsFragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_layout, detailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()


//        val intent = Intent(this, DetailsCharacter::class.java)
//        intent.putExtra("id", item.id.toInt())
//        Log.v("ID", item.id)
//        startActivity(intent)
//        val intent = Intent(this, DetailsActivity::class.java)
//        intent.putExtra("id", item.id)
//        startActivity(intent)

    }


}