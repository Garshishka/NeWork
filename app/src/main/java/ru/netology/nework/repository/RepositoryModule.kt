package ru.netology.nework.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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