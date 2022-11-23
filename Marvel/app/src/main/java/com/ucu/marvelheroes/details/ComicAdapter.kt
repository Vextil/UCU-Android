package com.ucu.marvelheroes.details


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelComic


class ComicAdapter(
    private val initialItems: List<MarvelComic>,
    var clickListener: OnComicItemClickListener)
    : RecyclerView.Adapter<ComicViewHolder>() {
    private val items = MutableList(initialItems.size) { initialItems[it] }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewHolder =
       ComicViewHolder(
           LayoutInflater.from(parent.context).inflate(R.layout.comicitem, parent, false)
       )

    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        holder.initialize(items[position],clickListener)
    }

    fun clear() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun update(newItems: List<MarvelComic>) {
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
class ComicViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    private var comicName = itemView.findViewById<TextView>(R.id.comicName)
    private var comicImage = itemView.findViewById<ImageView>(R.id.comicImage)


    fun initialize(item: MarvelComic, action: OnComicItemClickListener){
        comicName.text = item.title
        comicImage.load(item.thumbnailUrl)

        itemView.setOnClickListener {
            action.onItemClick(item, adapterPosition)
        }
    }
}
interface  OnComicItemClickListener{
    fun onItemClick(item: MarvelComic,position: Int)
}
