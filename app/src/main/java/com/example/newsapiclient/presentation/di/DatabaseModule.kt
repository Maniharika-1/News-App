package com.example.newsapiclient.presentation.di

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsapiclient.data.db.ArticleDao
import com.example.newsapiclient.data.db.ArticleDatabase
import com.example.newsapiclient.data.model.Article
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule() {

    @Singleton
    @Provides
    fun provideArticleDatabase(app: Application): ArticleDatabase {
        return Room.databaseBuilder(app.applicationContext,ArticleDatabase::class.java, "news_db")
            .fallbackToDestructiveMigration() //This will replace room to destructively replace database tables, if migrations,
            //that would migrate old database schemas to latest schema version are not found.
            .build()
    }

    @Singleton
    @Provides
    fun provideArticleDao(articleDatabase: ArticleDatabase) : ArticleDao {
        return articleDatabase.getArticleDao()
    }

}