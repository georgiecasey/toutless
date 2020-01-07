package com.georgiecasey.toutless.room.entities

import androidx.room.*

@Entity(tableName = "post")
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "toutless_post_id")
    val toutlessPostId: String,
    @ColumnInfo(name = "author_id")
    val authorId: String,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "post_text")
    val postText: String,
    @ColumnInfo(name = "post_time")
    val postTime: String
)

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Post?): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Post>?)

    @Query("DELETE FROM post")
    fun removeAll()

    @Query("SELECT * FROM post WHERE toutless_post_id = :toutlessPostId LIMIT 1")
    fun fetchById(toutlessPostId: String): Post?

    @Query("SELECT * FROM post")
    fun fetchAll(): List<Post>
}