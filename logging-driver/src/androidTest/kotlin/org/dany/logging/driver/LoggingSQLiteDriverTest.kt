package org.dany.logging.driver

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoggingSQLiteDriverTest {
    @Test
    fun validateDriver() {
        val loggedStatements = mutableListOf<String>()
        val driver = BundledSQLiteDriver().withLogging { _, sql ->
            loggedStatements.add(sql)
        }
        val connection = driver.open(":memory:")
        connection.execSQL("CREATE TABLE Test (data TEXT)")
        connection.close()

        assertEquals("CREATE TABLE Test (data TEXT)", loggedStatements.single())
    }
}