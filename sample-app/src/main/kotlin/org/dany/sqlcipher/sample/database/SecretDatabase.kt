package org.dany.sqlcipher.sample.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(entities = [SuperSecretEntity::class], version = 1, exportSchema = false)
abstract class SecretDatabase : RoomDatabase() {
    abstract fun getDao(): SecretsDao
}

@Entity
data class SuperSecretEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val text: String
)

@Dao
interface SecretsDao {
    @Insert
    suspend fun insert(secretEntity: SuperSecretEntity)

    @Query("SELECT * FROM SuperSecretEntity")
    fun flow(): Flow<List<SuperSecretEntity>>
}
