package com.aep.songsrfp2.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.aep.songsrfp2.data.remote.model.SongDto
import com.aep.songsrfp2.databinding.SongElementBinding
import com.squareup.picasso.Picasso

class SongViewHolder(
    private val binding: SongElementBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(song: SongDto){
        //Vinculamos las vistas con la información correspondiente
        binding.tvTitle.text = song.title
        binding.tvArtist.text = song.artist
        binding.tvAlbum.text = song.album
        binding.tvGenre.text = song.genre

        Picasso.get()
            .load(song.image)
            .into(binding.ivImage)
    }
}