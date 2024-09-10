package org.dany.copy.driver

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.sqlite.use
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CopyDriverTest {
    @Test
    fun validateDriver() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val driver = BundledSQLiteDriver()

        val firstPath = appContext.getDatabasePath("first.db")
        val firstConnection = driver.open(firstPath.absolutePath)
        firstConnection.execSQL("""
            CREATE TABLE IF NOT EXISTS Pet
            (id INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY(id AUTOINCREMENT))
            """.trimIndent()
        )
        firstConnection.execSQL("INSERT INTO Pet (name) VALUES ('Tom')")
        firstConnection.close()

        val secondPath = appContext.getDatabasePath("second.db")
        val secondConnection = driver
            .withCopyFrom { firstPath.inputStream() }
            .open(secondPath.absolutePath)
        secondConnection.prepare("SELECT * FROM Pet").use { stmt ->
            assertTrue(stmt.step())
            assertEquals(1, stmt.getLong(0))
        }
        secondConnection.close()
    }
}