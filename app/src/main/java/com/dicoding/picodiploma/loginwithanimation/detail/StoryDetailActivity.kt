package com.dicoding.picodiploma.loginwithanimation.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryDetailBinding
import com.dicoding.picodiploma.loginwithanimation.data.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    private val apiService: ApiService by lazy {
        RetrofitClient.getClient()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        Log.d("StoryDetailActivity", "Story ID received: $storyId")

        if (storyId != null) {
            fetchStoryDetails(storyId)
        } else {
            showErrorAndFinish("Story ID is missing")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun fetchStoryDetails(storyId: String) {
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = "Bearer ${UserPreference.getInstance(applicationContext.dataStore).getSession().first().token}"

                val response = apiService.getStoryDetail(token, storyId)
                if (response.isSuccessful) {
                    response.body()?.story?.let { story ->
                        Log.d("StoryDetailActivity", "Fetched story with ID: $storyId")  // Log the ID from API response
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.GONE
                            binding.storyTitle.text = story.name
                            binding.storyDescription.text = story.description
                            Glide.with(this@StoryDetailActivity)
                                .load(story.photoUrl)
                                .into(binding.storyImage)
                        }
                    } ?: showErrorAndFinish("Story data is empty")
                } else {
                    withContext(Dispatchers.Main) {
                        showErrorAndFinish("Failed to load story details")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorAndFinish("Error: ${e.message}")
                }
            }
        }
    }


    private fun showErrorAndFinish(message: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}
