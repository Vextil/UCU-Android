package com.ucu.marvelheroes.home

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.TypedValue
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
    private var items: MutableList<MarvelCharacter>,
    private var clickListener: OnCharacterItemClickListener
) : RecyclerView.Adapter<CharacterViewHolder>() {

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

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: MutableList<MarvelCharacter>) {
        val oldSize = items.size
        val newSize = newItems.size
        if (newItems == items) {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else {
            items = newItems.toMutableList()
            notifyDataSetChanged()
        }
    }

}

class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var characterName = itemView.findViewById<TextView>(R.id.characterName)
    private var characterImage = itemView.findViewById<ImageView>(R.id.characterImage)

    fun initialize(item: MarvelCharacter, action: OnCharacterItemClickListener) {
        characterName.text = item.name
        val r: Resources = itemView.context.resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            item.height,
            r.displayMetrics
        )
        characterImage.layoutParams.height = px.toInt()
        characterImage.load(item.thumbnailUrl)
        itemView.setOnClickListener {
            action.onItemClick(item, adapterPosition)
        }
    }

}

interface OnCharacterItemClickListener {
    fun onItemClick(item: MarvelCharacter, position: Int)
}
