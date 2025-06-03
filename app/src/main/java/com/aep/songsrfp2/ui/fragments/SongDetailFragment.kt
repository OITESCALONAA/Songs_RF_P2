package com.aep.songsrfp2.ui.fragments

import android.graphics.text.LineBreaker
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.aep.songsrfp2.R
import com.aep.songsrfp2.Utils.Constants
import com.aep.songsrfp2.Utils.isAtLeastAndroid
import com.aep.songsrfp2.application.SongsRFApp
import com.aep.songsrfp2.data.SongRepository
import com.aep.songsrfp2.databinding.FragmentSongDetailBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SongDetailFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentSongDetailBinding? = null
    private val binding get() = _binding!!

    private var songId: String? = null

    private lateinit var repository: SongRepository

    //Para GoogleMaps
    private lateinit var googleMap: GoogleMap
    private var lat: Double? = null
    private var lng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            songId = args.getString(Constants.SONG_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Aquí inflamos la vista correspondiente
        _binding = FragmentSongDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Instanciamos el repositorio desde la clase SongsRFApp
        repository = (requireActivity().application as SongsRFApp).repository

        lifecycleScope.launch {
            try {
                val songDetail = repository.getSong(songId)

                binding.apply {
                    tvTitle.text = songDetail.title

                    Picasso.get()
                        .load(songDetail.image)
                        .into(binding.ivImage)

                    tvArtist.text = songDetail.artist
                    tvAlbum.text = songDetail.album
                    tvGenre.text = songDetail.genre
                    tvReleaseDate.text = songDetail.releaseDate
                    tvRecordLabel.text = songDetail.recordLabel

                    isAtLeastAndroid(Q){
                        tvArtist.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        tvAlbum.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        tvGenre.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        tvReleaseDate.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        tvRecordLabel.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                    }

                    binding.ytpvVideo.addYouTubePlayerListener(object: AbstractYouTubePlayerListener(){
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            val uri = songDetail.video?.toUri()
                            val videoId = uri?.getQueryParameter("v")
                            if (!videoId.isNullOrEmpty()) {
                                youTubePlayer.loadVideo(videoId, 0f)
                            } else {
                                Log.e(Constants.LOGTAG, getString(R.string.invalid_video_url, songDetail.title))
                                Toast.makeText(requireContext(), getString(R.string.invalid_video_url, songDetail.title), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })

                    lat = songDetail.lat
                    lng = songDetail.lng
                    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this@SongDetailFragment)

                    //Para que el reproductor se enganche al ciclo de vida
                    lifecycle.addObserver(binding.ytpvVideo)
                }
            }catch (e: Exception){
                //Manejamos la excepción
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.connection_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }finally {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.ytpvVideo.release()
        _binding = null
    }

    companion object {
        //Instancia al fragment
        @JvmStatic
        fun newInstance(id: String) =
            SongDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.SONG_ID, id)
                }
            }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        createMarker()
    }

    private fun createMarker(){
        lat?.let { lat ->
            lng?.let { lng ->
                val coordinates = LatLng(lat, lng)

                //Generamos un marcador personalizado
                val marker = MarkerOptions()
                    .position(coordinates)
                    .title(getString(R.string.marker_option_title))
                    .snippet(getString(R.string.marker_option_location))

                googleMap.addMarker(marker)
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(coordinates, 15f),
                    4_000,
                    null
                )
            }
        }
    }
}