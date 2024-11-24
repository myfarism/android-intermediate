package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ListResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun getStories(): LiveData<ListResponse> = liveData {
        emit(ListResponse(error = false, message = "Loading..."))
        try {
            val stories = storyRepository.getStories()
            emit(stories)
        } catch (e: Exception) {
            emit(ListResponse(error = true, message = "Failed to fetch stories: ${e.message}"))
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    val pagedStories: LiveData<PagingData<ListStoryItem>> = liveData {
        val token = userRepository.getSession().first().token
        emitSource(storyRepository.getPagedStories("Bearer $token"))
    }

}
