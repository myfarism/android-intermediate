package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.camera.core.CameraEffect
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    val pagedStories: Flow<PagingData<ListStoryItem>> = flow {
        // Lakukan ini dalam coroutine agar bisa memanggil 'first()' dengan aman
        val token = userRepository.getSession().first().token

        // Setelah token diperoleh, kita bisa menggunakan token untuk memanggil repository
        if (token.isNotEmpty()) {
            // Emit data dari storyRepository
            emit(storyRepository.getPagedStories("Bearer $token").first()) // Pastikan mengambil data pertama
        }
    }
}
