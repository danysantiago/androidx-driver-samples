@file:JvmName("SQLCipherDriverKt")

package org.dany.sqlcipher.driver

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteException
import androidx.sqlite.execSQL
import androidx.sqlite.use

/**
 * A [SQLiteDriver] backed by [SQLCipher](https://github.com/sqlcipher/sqlcipher)
 *
 * When opening a connection, the given [onPassphrase] function is used to request for a key. If
 * the key is not valid then [open] will throw a [SQLCipherInvalidKeyException].
 *
 * @param onPassphrase Callback function invoked when this driver is requesting a passphrase to
 * unlock the database whose file name is received by this function.
 */
class SQLCipherDriver(
    private val onPassphrase: (String) -> String
) : SQLiteDriver {

    val threadingMode: Int
        get() = nativeThreadSafeMode()

    override fun open(fileName: String): SQLiteConnection {
        return open(fileName, SQLITE_OPEN_READWRITE or SQLITE_OPEN_CREATE)
    }

    fun open(fileName: String, @OpenFlag flags: Int): SQLiteConnection {
        val address = nativeOpen(fileName, flags)
        val connection = SQLCipherConnection(address)
        unlock(fileName, connection)
        return connection
    }

    private fun unlock(fileName: String, connection: SQLiteConnection) {
        connection.execSQL("PRAGMA key = ${onPassphrase.invoke(fileName)}")
        val isValidKey =
            try {
                connection.prepare("SELECT count(*) FROM sqlite_master").use { stmt ->
                    stmt.step() && !stmt.isNull(0)
                }
            } catch (ex : SQLiteException) {
                false
            }
        if (!isValidKey) {
            connection.close()
            throw SQLCipherInvalidKeyException()
        }
    }

    private companion object {
        init {
            System.loadLibrary("sqliteJni")
        }
    }
}

private external fun nativeThreadSafeMode(): Int

private external fun nativeOpen(name: String, openFlags: Int): Long
