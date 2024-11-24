package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.dicoding.picodiploma.loginwithanimation.data.api.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.api.ListResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.main.StoryPagingSource
import kotlinx.coroutines.flow.first

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStories(): ListResponse {
        val token = userPreference.getSession().first().token
        Log.d("StoryRepository", "Token used for API call: $token")

        return if (token.isNotEmpty()) {
            apiService.getStories("Bearer $token")
        } else {
            ListResponse(error = true, message = "Token is missing")
        }
    }

    suspend fun uploadStory(description: RequestBody, photo: MultipartBody.Part): AddStoryResponse {
        val token = userPreference.getSession().first().token
        Log.d("StoryRepository", "Token used for upload: $token")

        return if (token.isNotEmpty()) {
            apiService.addStory("Bearer $token", description, photo)
        } else {
            AddStoryResponse(error = true, message = "Token is missing")
        }
    }

    suspend fun getStoriesWithLocation(token: String): List<ListStoryItem> {
        val response = apiService.getStoriesWithLocation(token)
        if (response.error == false && response.listStory != null) {
            return response.listStory.filterNotNull() // Menghindari elemen null
        } else {
            throw Exception("Failed to fetch stories: ${response.message}")
        }
    }

    fun getPagedStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).liveData
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}
