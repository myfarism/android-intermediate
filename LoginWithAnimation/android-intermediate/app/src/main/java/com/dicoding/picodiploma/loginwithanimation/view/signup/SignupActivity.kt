package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.data.api.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val apiService: ApiService by lazy {
        RetrofitClient.getClient()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()

            val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            if (!isEmailValid) {
                binding.emailEditTextLayout.error = "Format email tidak valid"
            } else {
                binding.emailEditTextLayout.error = null
            }

            if (isEmailValid) {
                val name = binding.nameEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()
                showLoading(true)
                registerUser(name, email, password)
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !isLoading
    }

    private fun registerUser(name: String, email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.register(name, email, password)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showResponseDialog(response)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showErrorDialog(errorResponse.message ?: "Registrasi gagal.")
                }
            }
        }
    }

    private fun showResponseDialog(response: RegisterResponse) {
        AlertDialog.Builder(this).apply {
            setTitle("Registration Successful")
            setMessage(response.message ?: "User Created")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this@SignupActivity).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}
