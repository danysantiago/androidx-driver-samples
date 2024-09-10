package org.dany.sqlcipher.sample

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.dany.copy.driver.withCopyFrom
import org.dany.logging.driver.withLogging
import org.dany.sqlcipher.driver.SQLCipherDriver
import org.dany.sqlcipher.driver.SQLCipherInvalidKeyException
import org.dany.sqlcipher.sample.database.SecretDatabase
import org.dany.sqlcipher.sample.database.SuperSecretEntity

class SampleViewModel : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.ClosedDatabase)
    val uiState: State<UiState>
        get() = _uiState

    private val dbLock = Any()
    private var database: SecretDatabase? = null
    private var collectJob: Job? = null

    fun openDatabase(context: Context, passphrase: String) {
        collectJob = viewModelScope.launch {
            val db = synchronized(dbLock) {
                if (database == null) {
                    val dbPath = context.getDatabasePath("secrets.db").absolutePath
                    val driver = SQLCipherDriver(onPassphrase = { passphrase })
                        .withCopyFrom {
                            context.assets.open("secrets.db")
                        }
                        .withLogging { id, sql ->
                            Log.i("Sample App", "Connection #$id executing SQL: $sql")
                        }
                    database = Room.databaseBuilder<SecretDatabase>(context, dbPath)
                        .setDriver(driver)
                        .build()
                }
                checkNotNull(database)
            }
            try {
                db.getDao().flow().collect { list ->
                    _uiState.value = UiState.OpenDatabase(list)
                }
            } catch (ex: SQLCipherInvalidKeyException) {
                closeDatabase(UiState.LockedDatabase)
            }
        }
    }

    fun insertSecret(text: String) {
       viewModelScope.launch {
           val db = database ?: return@launch
           db.getDao().insert(SuperSecretEntity(0, text))
       }
    }

    fun closeDatabase() {
        closeDatabase(UiState.ClosedDatabase)
    }

    private fun closeDatabase(state: UiState) {
        _uiState.value = state
        collectJob?.cancel()
        database?.close()
        database = null
    }
}

sealed class UiState {
    class OpenDatabase(val secrets: List<SuperSecretEntity>) : UiState()
    data object ClosedDatabase : UiState()
    data object LockedDatabase : UiState()
}