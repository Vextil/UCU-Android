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
import kotlin.math.max


class CharacterAdapter(
    private var initialItems: List<MarvelCharacter>,
    private var clickListener: OnCharacterItemClickListener
) : RecyclerView.Adapter<BaseCharacterViewHolder>() {

    var shimmerCount = 6;
    var items = initialItems.toMutableList()

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        if (position < items.size) {
            return items[position].id.toLong()
        }
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        if (position < items.size) {
            return 0
        }
        return 1
    }

    override fun getItemCount(): Int {
        return items.size + shimmerCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCharacterViewHolder =
        if (viewType == 0) {
            CharacterViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.heroesitem, parent, false)
            )
        } else {
            CharacterShimmerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.shimmer_item, parent, false)
            )
        }

    override fun onBindViewHolder(holder: BaseCharacterViewHolder, position: Int) = when (holder) {
        is CharacterViewHolder -> holder.initialize(items[position], clickListener)
        is CharacterShimmerViewHolder -> holder.initialize()
        else -> throw IllegalArgumentException("Unknown view holder")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<MarvelCharacter>, reset: Boolean) {
        if (reset) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        } else {
            val oldSize = items.size
            val newSize = newItems.size
            items.addAll(newItems)
            notifyItemRangeInserted(oldSize, newSize)
        }
    }

    fun enableShimmer() {
        if (shimmerCount == 0) {
            shimmerCount = 6
            notifyItemRangeInserted(items.size, shimmerCount)
        }
    }

    fun disableShimmer() {
        if (shimmerCount > 0) {
            val oldShimmerCount = shimmerCount
            shimmerCount = 0
            notifyItemRangeRemoved(items.size, oldShimmerCount)
        }
    }

}

class CharacterViewHolder(itemView: View) : BaseCharacterViewHolder(itemView) {
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

class CharacterShimmerViewHolder(itemView: View) : BaseCharacterViewHolder(itemView) {
    fun initialize() {
    }

}

open class BaseCharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

interface OnCharacterItemClickListener {
    fun onItemClick(item: MarvelCharacter, position: Int)
}
