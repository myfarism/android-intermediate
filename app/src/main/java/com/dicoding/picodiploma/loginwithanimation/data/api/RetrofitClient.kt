package com.dicoding.picodiploma.loginwithanimation.data.api


import com.dicoding.picodiploma.loginwithanimation.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val baseURL = BuildConfig.BASE_URL

    fun getClient(token: String? = null): ApiService {
        val clientBuilder = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            if (!message.contains("binary")) {
                println("OkHttp: $message")
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        clientBuilder.addInterceptor(loggingInterceptor)

        token?.let {
            val authInterceptor = Interceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $it")
                    .header("Content-Type", "application/json; charset=UTF-8")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            clientBuilder.addInterceptor(authInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
            .create(ApiService::class.java)
    }

}
