package ivan.karpiuk.githubusers.api

import ivan.karpiuk.githubusers.dto.User
import ivan.karpiuk.githubusers.dto.UserDetails
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("/users")
    fun getUsers(): Call<List<User>>

    @GET("users")
    fun getUserSince(@Query("since") after: Int): Call<List<User>>

    @GET("users/{login}")
    fun getUserDetails(@Path("login") login: String?): Call<UserDetails>

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApi::class.java)
    }
}