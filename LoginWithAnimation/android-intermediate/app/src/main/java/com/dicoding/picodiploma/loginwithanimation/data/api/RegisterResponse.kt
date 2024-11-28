package com.dicoding.picodiploma.loginwithanimation.data.api

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@SerializedName("token")
	val token: String? = null,

	@field:SerializedName("loginResult")
	val loginResult: LoginData? = null
)
