package com.example.newsapiclient.presentation.di

import com.example.newsapiclient.domain.repository.NewsRepository
import com.example.newsapiclient.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideNewsHeadlineUseCase(newsRepository: NewsRepository) : GetNewsHeadlinesUseCase
    = GetNewsHeadlinesUseCase(newsRepository)

    @Singleton
    @Provides
    fun provideGetSearchedNewsHeadlineUseCase(newsRepository: NewsRepository) : GetSearchedNewsUseCase
            = GetSearchedNewsUseCase(newsRepository)

    @Singleton
    @Provides
    fun provideSaveNewsUseCase(newsRepository: NewsRepository) : SaveNewsUseCase
            = SaveNewsUseCase(newsRepository)

    @Singleton
    @Provides
    fun provideGetSavedNewsUseCase(newsRepository: NewsRepository) : GetSavedNewsUseCase
            = GetSavedNewsUseCase(newsRepository)

    @Singleton
    @Provides
    fun provideDeleteSavedNewsUseCase(newsRepository: NewsRepository) : DeleteSavedNewsUseCase
            = DeleteSavedNewsUseCase(newsRepository)
}