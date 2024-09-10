package org.dany.copy.driver

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import java.io.InputStream
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.moveTo
import kotlin.io.path.outputStream

typealias InputStreamFunction = (String) -> InputStream

/**
 * Extends this driver such that upon opening a connection to a database file and if the file
 * does not exist, then copy the bytes from the result of invoking [onCopy] into the location of
 * the database file, with the idea the [InputStream] returned by [onCopy] is an existing
 * database.
 *
 * @param onCopy A function that receives the file name of the database being opened and returns an
 * [InputStream] representing another database file that will be copied before actually opening
 * connections.
 */
fun SQLiteDriver.withCopyFrom(onCopy: InputStreamFunction): SQLiteDriver {
    return CopySQLiteDriver(this, onCopy)
}

private class CopySQLiteDriver(
    val delegate: SQLiteDriver,
    val inputStreamFunction: InputStreamFunction
) : SQLiteDriver by delegate {
    override fun open(fileName: String): SQLiteConnection {
        CopyHelper.maybeCopy(inputStreamFunction, fileName)
        return delegate.open(fileName)
    }
}

object CopyHelper {
    fun maybeCopy(
        inputFactory: InputStreamFunction,
        targetFileName: String
    ): Unit = synchronized(this) {
        if (targetFileName == ":memory:") {
            return
        }
        val targetPath = Path(targetFileName)
        if (targetPath.exists()) {
            return
        }
        val tmpPath = Path("${targetPath.absolutePathString()}.tmp")
        inputFactory.invoke(targetFileName).use { input ->
            tmpPath.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tmpPath.moveTo(targetPath)
    }
}
