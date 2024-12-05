package com.example.navigationmenu.adapters

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationmenu.R
import com.example.navigationmenu.models.Audio

class RecycleAdapter(val context: Context, val list: List<Audio>, val listener: onClick) :
    RecyclerView.Adapter<RecycleAdapter.Vh>() {
    inner class Vh(val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.image_recycle)
        private val musicName: TextView = itemView.findViewById(R.id.musicName_recycle)
        private val artist: TextView = itemView.findViewById(R.id.artist_recycle)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                listener.onItemClick(position)
            }
        }

        fun onBind(audio: Audio, position: Int) {
            val images = audio.data?.let { getAlbumArt(it) }
            if (images != null) {
                Glide.with(context).asBitmap().load(images).into(image)
            } else {
                Glide.with(context).load(R.drawable.musicplayer)
                    .into(image)
            }
            artist.text = audio.artist
            musicName.text = audio.musicName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(LayoutInflater.from(parent.context).inflate(R.layout.recycle_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)

    }

    interface onClick {
        fun onItemClick(position: Int)
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

}