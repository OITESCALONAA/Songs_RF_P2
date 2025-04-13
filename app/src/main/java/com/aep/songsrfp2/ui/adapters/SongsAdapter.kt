package com.aep.songsrfp2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aep.songsrfp2.data.remote.model.SongDto
import com.aep.songsrfp2.databinding.SongElementBinding

class SongsAdapter(
    private val songs: List<SongDto>, //Las canciones a desplegar
    private val onSongClick: (SongDto) -> Unit //Para los clicks
): RecyclerView.Adapter<SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)

        //Para el click
        holder.itemView.setOnClickListener {
            onSongClick(song)
        }
    }
}