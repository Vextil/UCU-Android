package com.ucu.marvelheroes.home


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



class CharacterAdapter(private val items: List<MarvelCharacter>, var clickListener: onCharacterItemClickListener) : RecyclerView.Adapter<CharacterViewHolder>() {

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
            var characterViewHolder = CharacterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.heroesitem, parent, false))
            return characterViewHolder
        }


        override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
            holder.initialize(items.get(position),clickListener)
        }

    }
    class CharacterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var characterName = itemView.findViewById<TextView>(R.id.characterName)
        var characterImage = itemView.findViewById<ImageView>(R.id.characterImage)



        fun initialize(item: MarvelCharacter, action:onCharacterItemClickListener){
            val viewshadow = itemView.findViewById<View>(R.id.viewshadow)
            val drawable = viewshadow.background.mutate() as GradientDrawable
            drawable.color = ContextCompat.getColorStateList(itemView.context, R.color.__c_blue_300)
            viewshadow.background = drawable;
            characterName.text = item.name

//            substring before the last dot
            val url = item.thumbnailUrl?.substringBeforeLast("jpg")

            characterImage.load("$url.jpg")


            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }

        }

    }
    interface  onCharacterItemClickListener{
        fun onItemClick(item: MarvelCharacter,position: Int)
    }
