package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.auth.AuthPair
import ru.netology.nework.dto.Post

interface ApiService {

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{post_id}/before")
    suspend fun getBefore(
        @Path("post_id") id: String,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @FormUrlEncoded
    @POST("users/authentication/")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("password") password: String
    ): Response<AuthPair>

    @FormUrlEncoded
    @POST("users/registration/")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Response<AuthPair>
}