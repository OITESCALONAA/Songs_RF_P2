package com.aep.songsrfp2.ui.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.aep.songsrfp2.R
import com.aep.songsrfp2.Utils.message
import com.aep.songsrfp2.databinding.FragmentLoginBinding
import com.aep.songsrfp2.ui.MainActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    //Para firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //Propiedades para el email y la contraseña
    private var email = ""
    private var contrasena = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Aquí inflamos la vista correspondiente
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Bloquea la rotacion y especifica la vista en modo retrato
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Instanciamos el objeto firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //Si ya estaba previamente autenticado un usuario
        //Lo mandamos a la pantalla principal
        if(firebaseAuth.currentUser != null)
            actionLoginSuccessful()

        binding.btnLogin.setOnClickListener {
            if(!validateFields()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE
            authenticateUser(email, contrasena)
        }

        binding.btnRegistrarse.setOnClickListener {
            if(!validateFields()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE
            createUser(email, contrasena)
        }

        binding.tvRestablecerPassword.setOnClickListener {
            resetPassword()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateFields(): Boolean{
        email = binding.tietEmail.text.toString().trim()  //Elimina los espacios en blanco
        contrasena = binding.tietContrasena.text.toString().trim()

        //Verifica que el campo de correo no esté vacío
        if(email.isEmpty()){
            binding.tietEmail.error = getString(R.string.email_required)
            binding.tietEmail.requestFocus()
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tietEmail.error = getString(R.string.email_format_not_valid)
            binding.tietEmail.requestFocus()
            return false
        }

        //Verifica que el campo de la contraseña no esté vacía y tenga al menos 6 caracteres
        if(contrasena.isEmpty()){
            binding.tietContrasena.error = getString(R.string.password_required)
            binding.tietContrasena.requestFocus()
            return false
        }else if(contrasena.length < 6){
            binding.tietContrasena.error = getString(R.string.password_min_length)
            binding.tietContrasena.requestFocus()
            return false
        }
        return true
    }

    private fun handleErrors(task: Task<AuthResult>){
        var errorCode = ""

        try{
            errorCode = (task.exception as FirebaseAuthException).errorCode
        }catch(e: Exception){
            e.printStackTrace()
        }

        when(errorCode){
            getString(R.string.error_invalid_email) -> {
                requireActivity().message(getString(R.string.error_email_format))
                binding.tietEmail.error = getString(R.string.error_email_format)
                binding.tietEmail.requestFocus()
            }
            getString(R.string.error_wrong_password) -> {
                requireActivity().message(getString(R.string.error_password_not_valid))
                binding.tietContrasena.error = getString(R.string.error_password_not_valid)
                binding.tietContrasena.requestFocus()
                binding.tietContrasena.setText("")

            }
            getString(R.string.error_account_exists_with_different_credential) -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                requireActivity().message(getString(R.string.error_different_signin_credentials))
            }
            getString(R.string.error_email_already_in_use) -> {
                requireActivity().message(getString(R.string.error_email_already_registered))
                binding.tietEmail.error = (getString(R.string.error_email_already_registered))
                binding.tietEmail.requestFocus()
            }
            getString(R.string.error_user_token_expired) -> {
                requireActivity().message(getString(R.string.error_session_expired))
            }
            getString(R.string.error_user_not_found) -> {
                requireActivity().message(getString(R.string.error_user_not_exists))
            }
            getString(R.string.error_weak_password) -> {
                requireActivity().message(getString(R.string.error_password_not_valid))
                binding.tietContrasena.error = getString(R.string.password_min_length)
                binding.tietContrasena.requestFocus()
            }
            getString(R.string.no_network) -> {
                requireActivity().message(getString(R.string.error_no_network))
            }
            else -> {
                requireActivity().message(getString(R.string.error_authentication_unsuccessful))
            }
        }

    }

    private fun actionLoginSuccessful(){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                SongsListFragment()
            )
            .commit()
        (requireActivity()as? MainActivity)?.isUserLogged(true)
    }

    private fun authenticateUser(usr: String, psw: String){
        firebaseAuth.signInWithEmailAndPassword(usr, psw).addOnCompleteListener { authResult ->
            if(authResult.isSuccessful) {
                requireActivity().message(getString(R.string.authentication_successful))
                actionLoginSuccessful()
            }else{
                //Para que no se muestre el progress bar
                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)
            }
        }
    }

    private fun createUser(usr: String, psw: String){
        firebaseAuth.createUserWithEmailAndPassword(usr, psw).addOnCompleteListener { authResult ->
            if(authResult.isSuccessful) {
                //Si se pudo registrar el usuario nuevo

                //Mandamos un correo de verificacion
                firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                    requireActivity().message(getString(R.string.verification_email_sent))
                }?.addOnFailureListener {
                    requireActivity().message(getString(R.string.verification_email_not_sent))
                }

                requireActivity().message(getString(R.string.user_created_successfully))
                actionLoginSuccessful()
            }else{
                //Para que no se muestre el progress bar
                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)
            }
        }
    }

    private fun resetPassword(){
        //Genero un edit text programaticamente
        val resetMail = EditText(requireActivity())
        resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.restore_password))
            .setMessage(getString(R.string.restore_message))
            .setView(resetMail)
            .setPositiveButton(getString(R.string.restore_ok_button)) { _, _ ->
                val mail =  resetMail.text.toString()
                if(mail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                    //Enviamos el enlace de restablecimiento
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener {
                        requireActivity().message(getString(R.string.restoration_email_sent))
                    }.addOnFailureListener {
                        requireActivity().message(getString(R.string.restoration_email_not_sent))
                    }
                }else{
                    requireActivity().message(getString(R.string.email_required))
                }
            }
            .setNegativeButton(getString(R.string.restore_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}