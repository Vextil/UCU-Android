package com.ucu.marvelheroes.moreComics


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
import com.ucu.marvelheroes.home.CharacterViewHolder
import com.ucu.marvelheroes.home.OnCharacterItemClickListener


class MoreComicsAdapter(
        private val initialItems: List<MarvelComic>,
        private var clickListener: onComicItemClickListener
    ) : RecyclerView.Adapter<ComicViewHolder>() {

        private val items = MutableList(initialItems.size) { initialItems[it] }
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewHolder {
        var comicViewHolder = ComicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_viewcomicsitem, parent, false))
        return comicViewHolder
    }


    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        holder.initialize(items.get(position),clickListener)
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
    var comicName = itemView.findViewById<TextView>(R.id.comicName)
    var comicImage = itemView.findViewById<ImageView>(R.id.comicImage)
    var comicIssueNumber = itemView.findViewById<TextView>(R.id.comicIssueNumber)


    fun initialize(item: MarvelComic, action: onComicItemClickListener){
        comicName.text = item.title

        val url = item.thumbnailUrl?.replace("http", "https")
        comicImage.load(url)
        comicIssueNumber.text = item.issueNumber.toString()



        itemView.setOnClickListener {
            action.onComicItemClick(item, adapterPosition)
        }

    }

}
interface  onComicItemClickListener{
    fun onComicItemClick(item: MarvelComic, position: Int)
}
