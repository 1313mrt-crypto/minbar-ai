package com.sokhanara.app.data.local.dao

import androidx.room.*
import com.sokhanara.app.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

/**
 * Project DAO
 */
@Dao
interface ProjectDao {
    
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>
    
    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)
    
    @Update
    suspend fun updateProject(project: ProjectEntity)
    
    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}