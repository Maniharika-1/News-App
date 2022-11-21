package com.example.newsapiclient.presentation.di

import android.app.Application
import com.example.newsapiclient.domain.usecase.*
import com.example.newsapiclient.presentation.viewmodel.NewsViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FactoryModule {

    @Singleton
    @Provides
    fun provideNewsViewModelFactory(app: Application, getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    getSearchedNewsUseCase: GetSearchedNewsUseCase,
    saveNewsUseCase: SaveNewsUseCase,
    getSavedNewsUseCase: GetSavedNewsUseCase,
    deleteSavedNewsUseCase: DeleteSavedNewsUseCase): NewsViewModelFactory
    = NewsViewModelFactory(app, getNewsHeadlinesUseCase, getSearchedNewsUseCase, saveNewsUseCase, getSavedNewsUseCase,
    deleteSavedNewsUseCase)
}