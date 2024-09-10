@file:JvmName("SQLCipherConnectionKt")

package org.dany.sqlcipher.driver

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.throwSQLiteException
import org.dany.sqlcipher.driver.ResultCode.SQLITE_MISUSE

internal class SQLCipherConnection(private val connectionPointer: Long) :
    SQLiteConnection {

    @Volatile private var isClosed = false

    override fun prepare(sql: String): SQLiteStatement {
        if (isClosed) {
            throwSQLiteException(SQLITE_MISUSE, "connection is closed")
        }
        val statementPointer = nativePrepare(connectionPointer, sql)
        return SQLCipherStatement(connectionPointer, statementPointer)
    }

    override fun close() {
        if (!isClosed) {
            nativeClose(connectionPointer)
        }
        isClosed = true
    }
}

private external fun nativePrepare(pointer: Long, sql: String): Long

private external fun nativeClose(pointer: Long)
