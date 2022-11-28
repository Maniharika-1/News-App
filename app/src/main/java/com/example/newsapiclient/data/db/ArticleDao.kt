package com.example.newsapiclient.data.db

import androidx.room.*
import com.example.newsapiclient.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(article: Article)

    @Query("Select * from articles")
    fun getAllArticles() : Flow<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}