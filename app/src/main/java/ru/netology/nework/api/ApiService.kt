package ru.netology.nework.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.auth.AuthPair
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User

interface ApiService {

    //POSTS
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(
        @Path("post_id") id: String,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("posts/{post_id}/before")
    suspend fun getBefore(
        @Path("post_id") id: String,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Header("auth") auth: String, @Body post: Post): Response<Post>

    @POST("posts/{id}/likes ")
    suspend fun likeById(@Header("auth") auth: String, @Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}/likes ")
    suspend fun dislikeById(@Header("auth") auth: String, @Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Header("auth") auth: String, @Path("id") id: Int): Response<Unit>

    //MY WALL
    @GET("my/wall")
    suspend fun getMyWall(@Header("auth") auth: String): Response<List<Post>>

    @GET("my/wall/latest")
    suspend fun getMyWallLatest(@Header("auth") auth: String, @Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{post_id}/after")
    suspend fun getMyWallAfter(
        @Header("auth") auth: String,
        @Path("post_id") id: String,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("my/wall/{post_id}/before")
    suspend fun getMyWallBefore(
        @Header("auth") auth: String,
        @Path("post_id") id: String,
        @Query("count") count: Int,
    ): Response<List<Post>>


    //MEDIA UPLOAD
    @Multipart
    @POST("media")
    suspend fun upload(
        @Header("auth") auth: String,
        @Part file: MultipartBody.Part
    ): Response<MediaUpload>

    //JOBS
    @GET("my/jobs/")
    suspend fun getMyJobs(@Header("auth") auth: String): Response<List<Job>>

    @GET("{user_id}/jobs/")
    suspend fun getPersonJobs(@Path("user_id") id: String): Response<List<Job>>

    @POST("my/jobs/")
    suspend fun addNewJob(@Header("auth") auth: String, @Body job: Job) :Response<Job>

    @DELETE("my/jobs/{job_id}/")
    suspend fun removeJob(@Header("auth") auth: String, @Path("job_id") id: String) :Response<Unit>

    //USERS
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

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
        @Field("name") name: String,
    ): Response<AuthPair>

    @POST("users/registration/")
    suspend fun registerUserWithAvatar(
        @Body body: MultipartBody
    ): Response<AuthPair>
}