package com.aep.songsrfp2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aep.songsrfp2.R
import com.aep.songsrfp2.Utils.Constants
import com.aep.songsrfp2.databinding.ActivityMainBinding
import com.aep.songsrfp2.ui.fragments.LoginFragment
import com.aep.songsrfp2.ui.fragments.SongsListFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //Para firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Instalar Splash Screen
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Obtener instancia de Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //Primera ejecución de la activity
        if(savedInstanceState == null){
            //Si el usuario esta logeado, ir a la lista de canciones
            if(firebaseAuth.currentUser != null) {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        SongsListFragment()
                    )
                    .commit()
                isUserLogged(true)
            }
            else //Si no, ir a la pantalla de Login
            {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        LoginFragment()
                    )
                    .commit()
                isUserLogged(false)
            }
        }

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    LoginFragment()
                )
                .commit()
            isUserLogged(false)
        }
    }

    fun isUserLogged(isUserLogged: Boolean){
        if(isUserLogged)
            binding.btnLogout.visibility = View.VISIBLE
        else
            binding.btnLogout.visibility = View.GONE
    }
}