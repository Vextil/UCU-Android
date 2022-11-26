package com.ucu.marvelheroes.moreComics


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import com.ucu.marvelheroes.home.CharacterShimmerViewHolder
import com.ucu.marvelheroes.home.CharacterViewHolder
import com.ucu.marvelheroes.home.OnCharacterItemClickListener


class MoreComicsAdapter(
    private val initialItems: List<MarvelComic>,
    private var clickListener: onComicItemClickListener
) : RecyclerView.Adapter<BaseComicViewHolder>() {

    var shimmerCount = 6;
    private val items = initialItems.toMutableList()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseComicViewHolder =
        if (viewType == 0) {
            ComicViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_viewcomicsitem, parent, false)
            )
        } else {
            ComicShimmerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.shimmer_item, parent, false)
            )
        }


    override fun onBindViewHolder(holder: BaseComicViewHolder, position: Int) = when (holder) {
        is ComicViewHolder -> holder.initialize(items.get(position), clickListener)
        is ComicShimmerViewHolder -> holder.initialize()
        else -> throw IllegalArgumentException("Unknown view holder")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(newItems: List<MarvelComic>) {
        val oldSize = items.size
        val newSize = newItems.size
        items.clear()
        items.addAll(newItems)
        if (oldSize == 0) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
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

class ComicViewHolder(itemView: View) : BaseComicViewHolder(itemView) {
    var comicName = itemView.findViewById<TextView>(R.id.comicName)
    var comicImage = itemView.findViewById<ImageView>(R.id.comicImage)
    var comicIssueNumber = itemView.findViewById<TextView>(R.id.comicIssueNumber)

    fun initialize(item: MarvelComic, action: onComicItemClickListener) {
        comicName.text = item.title
        comicImage.load(item.thumbnailUrl)
        comicIssueNumber.text = item.issueNumber.toString()
        itemView.setOnClickListener {
            action.onComicItemClick(item, adapterPosition)
        }
    }
}

class ComicShimmerViewHolder(itemView: View) : BaseComicViewHolder(itemView) {
    fun initialize() {
    }
}

open class BaseComicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
}


interface onComicItemClickListener {
    fun onComicItemClick(item: MarvelComic, position: Int)
}
