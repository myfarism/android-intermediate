package com.dicoding.picodiploma.loginwithanimation.data.api

import com.google.gson.annotations.SerializedName

data class StoryDetailResponse(
	@SerializedName("error")
	val error: Boolean,

	@SerializedName("message")
	val message: String,

	@SerializedName("story")
	val story: Story
)

data class Story(
	@SerializedName("id")
	val id: String,

	@SerializedName("name")
	val name: String,

	@SerializedName("description")
	val description: String,

	@SerializedName("photoUrl")
	val photoUrl: String,

	@SerializedName("createdAt")
	val createdAt: String,

	@SerializedName("lat")
	val lat: Double,

	@SerializedName("lon")
	val lon: Double
)
