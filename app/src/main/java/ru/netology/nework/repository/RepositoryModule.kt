package ru.netology.nework.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.repository.media.MediaRepository
import ru.netology.nework.repository.media.MediaRepositoryImpl
import ru.netology.nework.repository.posts.PostRepository
import ru.netology.nework.repository.posts.PostRepositoryImpl
import ru.netology.nework.repository.users.UsersRepository
import ru.netology.nework.repository.users.UsersRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindsPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    @Singleton
    @Binds
    fun bindsMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository

    @Singleton
    @Binds
    fun bindsUsersRepository(
        usersRepositoryImpl: UsersRepositoryImpl
    ): UsersRepository
}