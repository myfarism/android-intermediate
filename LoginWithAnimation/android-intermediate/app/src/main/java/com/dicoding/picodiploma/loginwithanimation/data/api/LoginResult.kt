package com.dicoding.picodiploma.loginwithanimation.data.api

import com.google.gson.annotations.SerializedName

data class LoginData(
	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
