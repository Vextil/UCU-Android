package com.ucu.marvelheroes.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.auth.AuthActivity
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.source.interfaces.IAuthRepository
import com.ucu.marvelheroes.databinding.FragmentHomeBinding
import com.ucu.marvelheroes.details.CharacterDetailsFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(), OnCharacterItemClickListener {

    private lateinit var adapter: CharacterAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val viewModel: HomeViewModel by viewModel()
    private val authRepository: IAuthRepository by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        recycler.setHasFixedSize(true)
        recycler.layoutManager = layoutManager
        adapter = CharacterAdapter(viewModel.characters.value ?: mutableListOf(), this)
        recycler.adapter = adapter
        recycler.addOnScrollListener(OnScrollListener(viewModel, layoutManager))
        val optionsButton = requireView().findViewById<Button>(R.id.optionsButton)
        optionsButton.setOnClickListener {
            showPopup(it)
        }
        if (viewModel.characters.value.isNullOrEmpty()) {
            viewModel.load(null)
        }
        viewModel.characters.observe(viewLifecycleOwner) {
            adapter.update(it)
        }
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

    fun showPopup(v: View) {
        val popupMenu = PopupMenu(v.context, v)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.logout -> {
                    authRepository.logout()
                    goToLogin()
                }
            }
            true
        }
    }

    fun goToLogin() {
        val i = Intent(context, AuthActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        activity?.finish()
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
                viewModel.loadMore()
            }
        }
    }


}