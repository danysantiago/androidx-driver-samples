@file:JvmName("SQLCipherStatementKt")

package org.dany.sqlcipher.driver

import androidx.sqlite.SQLiteStatement
import androidx.sqlite.throwSQLiteException
import org.dany.sqlcipher.driver.ResultCode.SQLITE_MISUSE

internal class SQLCipherStatement(
    private val connectionPointer: Long,
    private val statementPointer: Long
) : SQLiteStatement {

    @Volatile private var isClosed = false

    override fun bindBlob(index: Int, value: ByteArray) {
        throwIfClosed()
        nativeBindBlob(statementPointer, index, value)
    }

    override fun bindDouble(index: Int, value: Double) {
        throwIfClosed()
        nativeBindDouble(statementPointer, index, value)
    }

    override fun bindLong(index: Int, value: Long) {
        throwIfClosed()
        nativeBindLong(statementPointer, index, value)
    }

    override fun bindText(index: Int, value: String) {
        throwIfClosed()
        nativeBindText(statementPointer, index, value)
    }

    override fun bindNull(index: Int) {
        throwIfClosed()
        nativeBindNull(statementPointer, index)
    }

    override fun getBlob(index: Int): ByteArray {
        throwIfClosed()
        return nativeGetBlob(statementPointer, index)
    }

    override fun getDouble(index: Int): Double {
        throwIfClosed()
        return nativeGetDouble(statementPointer, index)
    }

    override fun getLong(index: Int): Long {
        throwIfClosed()
        return nativeGetLong(statementPointer, index)
    }

    override fun getText(index: Int): String {
        throwIfClosed()
        return nativeGetText(statementPointer, index)
    }

    override fun isNull(index: Int): Boolean {
        throwIfClosed()
        return nativeGetColumnType(statementPointer, index) == COLUMN_TYPE_NULL
    }

    override fun getColumnCount(): Int {
        throwIfClosed()
        return nativeGetColumnCount(statementPointer)
    }

    override fun getColumnName(index: Int): String {
        throwIfClosed()
        return nativeGetColumnName(statementPointer, index)
    }

    override fun step(): Boolean {
        throwIfClosed()
        return nativeStep(statementPointer)
    }

    override fun reset() {
        throwIfClosed()
        nativeReset(statementPointer)
    }

    override fun clearBindings() {
        throwIfClosed()
        nativeClearBindings(statementPointer)
    }

    override fun close() {
        if (!isClosed) {
            nativeClose(statementPointer)
        }
        isClosed = true
    }

    private fun throwIfClosed() {
        if (isClosed) {
            throwSQLiteException(SQLITE_MISUSE, "statement is closed")
        }
    }

    private companion object {
        private const val COLUMN_TYPE_INTEGER = 1
        private const val COLUMN_TYPE_FLOAT = 2
        private const val COLUMN_TYPE_TEXT = 3
        private const val COLUMN_TYPE_BLOB = 4
        private const val COLUMN_TYPE_NULL = 5
    }
}

private external fun nativeBindBlob(pointer: Long, index: Int, value: ByteArray)

private external fun nativeBindDouble(pointer: Long, index: Int, value: Double)

private external fun nativeBindLong(pointer: Long, index: Int, value: Long)

private external fun nativeBindText(pointer: Long, index: Int, value: String)

private external fun nativeBindNull(pointer: Long, index: Int)

private external fun nativeStep(pointer: Long): Boolean

private external fun nativeGetBlob(pointer: Long, index: Int): ByteArray

private external fun nativeGetDouble(pointer: Long, index: Int): Double

private external fun nativeGetLong(pointer: Long, index: Int): Long

private external fun nativeGetText(pointer: Long, index: Int): String

private external fun nativeGetColumnType(pointer: Long, index: Int): Int

private external fun nativeGetColumnCount(pointer: Long): Int

private external fun nativeGetColumnName(pointer: Long, index: Int): String

private external fun nativeReset(pointer: Long)

private external fun nativeClearBindings(pointer: Long)

private external fun nativeClose(pointer: Long)
