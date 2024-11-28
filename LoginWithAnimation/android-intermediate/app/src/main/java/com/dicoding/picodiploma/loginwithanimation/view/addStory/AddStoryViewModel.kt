package com.dicoding.picodiploma.loginwithanimation.view.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.dicoding.picodiploma.loginwithanimation.data.api.AddStoryResponse

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()

    fun uploadStory(description: RequestBody, photo: MultipartBody.Part): LiveData<AddStoryResponse> {
        return liveData {
            isLoading.postValue(true)
            try {
                val response = storyRepository.uploadStory(description, photo)
                emit(response)
            } catch (e: Exception) {
                emit(AddStoryResponse(error = true, message = e.message ?: "An error occurred"))
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}
