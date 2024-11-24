package com.dicoding.picodiploma.loginwithanimation.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.api.Story
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    fun fetchStoriesWithLocation(token: String) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStoriesWithLocation(token)
                _stories.value = response.map { listItem ->
                    Story(
                        id = listItem.id ?: "",
                        name = listItem.name ?: "Unknown",
                        description = listItem.description ?: "",
                        photoUrl = listItem.photoUrl ?: "",
                        createdAt = listItem.createdAt ?: "",
                        lat = listItem.lat ?: 0.0,
                        lon = listItem.lon ?: 0.0
                    )
                }
            } catch (e: Exception) {
                Log.e("MapsViewModel", "Error fetching stories: ${e.message}")
            }
        }
    }
}
