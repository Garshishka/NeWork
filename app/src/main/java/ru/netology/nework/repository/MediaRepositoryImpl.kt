package ru.netology.nework.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.MediaUpload
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
):MediaRepository {
    override suspend fun upload(file: File, authToken: String): MediaUpload {
        try {
            val data =
                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())

            val response = apiService.upload(authToken, data)
            if (!response.isSuccessful) {
                throw RuntimeException(
                    response.code().toString()
                )
            }
            return response.body() ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}