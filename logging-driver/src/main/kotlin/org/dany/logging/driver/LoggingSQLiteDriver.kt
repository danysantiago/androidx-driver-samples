package org.dany.logging.driver

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import java.util.concurrent.atomic.AtomicInteger

typealias LogSQLFunction = (Int, String) -> Unit

/**
 * Extends this driver such that everytime a SQL statement is prepared for execution from any
 * connection created by this driver, then the [onLog] function is invoked with the SQL string.
 *
 * @param onLog A function that receives a connection id and the SQL string when statements are
 * prepared.
 */
fun SQLiteDriver.withLogging(
    onLog: LogSQLFunction
): SQLiteDriver {
    return LoggingSQLiteDriver(this, onLog)
}

private class LoggingSQLiteDriver(
    private val delegate: SQLiteDriver,
    private val logFunction: LogSQLFunction
) : SQLiteDriver by delegate {

    private var connectionId = AtomicInteger()

    override fun open(fileName: String): SQLiteConnection {
        return LoggingSQLiteConnection(
            delegate = delegate.open(fileName),
            id = connectionId.incrementAndGet(),
            logFunction = logFunction
        )
    }
}

private class LoggingSQLiteConnection(
    private val delegate: SQLiteConnection,
    private val id: Int,
    private val logFunction: LogSQLFunction
) : SQLiteConnection by delegate {
    override fun prepare(sql: String): SQLiteStatement {
        logFunction.invoke(id, sql)
        return LoggingSQLiteStatement(delegate.prepare(sql), logFunction)
    }
}

private class LoggingSQLiteStatement(
    private val delegate: SQLiteStatement,
    private val logFunction: LogSQLFunction
) : SQLiteStatement by delegate


