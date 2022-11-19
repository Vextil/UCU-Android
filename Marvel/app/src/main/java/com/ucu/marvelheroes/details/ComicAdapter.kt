package com.ucu.marvelheroes.details


import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.data.domain.model.MarvelCharacter
import com.ucu.marvelheroes.data.domain.model.MarvelComic
import kotlin.math.log


class ComicAdapter(private val items: List<MarvelComic>, var clickListener: onComicItemClickListener) : RecyclerView.Adapter<ComicViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewHolder {
        var comicViewHolder = ComicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comicitem, parent, false))
        return comicViewHolder
    }


    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        holder.initialize(items.get(position),clickListener)
    }

}
class ComicViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    var comicName = itemView.findViewById<TextView>(R.id.comicName)
    var comicImage = itemView.findViewById<ImageView>(R.id.comicImage)


    fun initialize(item: MarvelComic, action: onComicItemClickListener){
        comicName.text = item.title

        val url = item.thumbnailUrl?.replace("http", "https")
        comicImage.load(url)



        itemView.setOnClickListener {
            action.onItemClick(item, adapterPosition)
        }

    }

}
interface  onComicItemClickListener{
    fun onItemClick(item: MarvelComic,position: Int)
}
