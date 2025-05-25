package com.aep.songsrfp2.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aep.songsrfp2.R
import com.aep.songsrfp2.Utils.Constants
import com.aep.songsrfp2.application.SongsRFApp
import com.aep.songsrfp2.data.SongRepository
import com.aep.songsrfp2.databinding.FragmentSongsListBinding
import com.aep.songsrfp2.ui.adapters.SongsAdapter
import kotlinx.coroutines.launch

class SongsListFragment : Fragment() {
    private var _binding: FragmentSongsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: SongRepository

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Aquí inflamos la vista correspondiente
        _binding = FragmentSongsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    //Aquí ya está el fragment en pantalla
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Instanciamos el repositorio desde la clase SongsRFApp
        repository = (requireActivity().application as SongsRFApp).repository

        lifecycleScope.launch {
            try {
                val songs = repository.getSongs()

                binding.rvSongs.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = SongsAdapter(songs){ selectedSong ->
                        //Click de cada juego
                        Log.d(Constants.LOGTAG,
                            context.getString(R.string.game_click, selectedSong.title))
                        //Pasamos al siguiente fragment con el id del juego seleccionado
                        selectedSong.id?.let{ id ->
                            //Cargamos el contenido en memoria
                            mediaPlayer = MediaPlayer.create(this@SongsListFragment.requireContext(), R.raw.song_selected)

                            mediaPlayer.setOnCompletionListener {
                                //Liberar recursos
                                mediaPlayer.release()

                                //ir a la vista del detalle
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.fragment_container,
                                        SongDetailFragment.newInstance(id)
                                    )
                                    .addToBackStack(null)
                                    .commit()
                            }

                            //Le damos play al efecto de sonido
                            mediaPlayer.start()
                        }
                    }
                }
            } catch (e: Exception) {
                //Manejamos la excepción
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.connection_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } finally {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        _binding = null
    }
}