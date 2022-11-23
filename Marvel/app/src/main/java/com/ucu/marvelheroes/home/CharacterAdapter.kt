package com.ucu.marvelheroes.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter

class CharacterAdapter(
    private val initialItems: List<MarvelCharacter>,
    private var clickListener: OnCharacterItemClickListener
) : RecyclerView.Adapter<CharacterViewHolder>() {

    private val items = MutableList(initialItems.size) { initialItems[it] }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder =
        CharacterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.heroesitem, parent, false)
        )

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.initialize(items[position], clickListener)
    }

    fun clear() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun update(newItems: List<MarvelCharacter>) {
        val oldSize = items.size
        val newSize = newItems.size
        if (newSize > oldSize) {
            items.addAll(newItems.subList(oldSize, newSize))
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else {
            items.clear()
            items.addAll(newItems)
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        }
    }

}

class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var characterName = itemView.findViewById<TextView>(R.id.characterName)
    private var characterImage = itemView.findViewById<ImageView>(R.id.characterImage)

    fun initialize(item: MarvelCharacter, action: OnCharacterItemClickListener) {
        characterName.text = item.name
        characterImage.load(item.thumbnailUrl)

        itemView.setOnClickListener {
            action.onItemClick(item, adapterPosition)
        }
    }

}

interface OnCharacterItemClickListener {
    fun onItemClick(item: MarvelCharacter, position: Int)
}
