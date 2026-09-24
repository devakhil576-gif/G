package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PoetryDao {
    @Query("SELECT * FROM poetry_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<PoetryProjectEntity>>

    @Query("SELECT * FROM poetry_projects WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteProjects(): Flow<List<PoetryProjectEntity>>

    @Query("SELECT * FROM poetry_projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: Long): Flow<PoetryProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: PoetryProjectEntity): Long

    @Update
    suspend fun updateProject(project: PoetryProjectEntity)

    @Query("DELETE FROM poetry_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("UPDATE poetry_projects SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: Long)
}
