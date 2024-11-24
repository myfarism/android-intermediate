package com.dicoding.picodiploma.loginwithanimation.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        if (storyId != null) {
            fetchStoryDetails(storyId)
        } else {
            Toast.makeText(this, "Story ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchStoryDetails(storyId: String) {
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = "Bearer ${UserPreference.getInstance(applicationContext.dataStore).getSession().first().token}"

                val response = apiService.getStoryDetail(token, storyId)
                if (response.isSuccessful) {
                    val story = response.body()?.story
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.storyTitle.text = story?.name
                        binding.storyDescription.text = story?.description
                        Glide.with(this@StoryDetailActivity)
                            .load(story?.photoUrl)
                            .into(binding.storyImage)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@StoryDetailActivity, "Failed to load story details", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@StoryDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}
