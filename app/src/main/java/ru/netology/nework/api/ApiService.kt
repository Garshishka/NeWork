package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.netology.nework.auth.AuthPair

interface ApiService {

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