package org.dany.sqlcipher.driver

import androidx.sqlite.execSQL
import androidx.sqlite.use
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class SQLCipherDriverTest {
    @Test
    fun validateDriver() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.deleteDatabase("encrypted.db")

        val driver = SQLCipherDriver(onPassphrase = { "mofongo" })
        val connection = driver.open(appContext.getDatabasePath("encrypted.db").absolutePath)
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS Pet
            (id INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY(id AUTOINCREMENT))
            """.trimIndent()
        )
        connection.execSQL("INSERT INTO Pet (name) VALUES ('Tom')")
        connection.prepare("SELECT * FROM Pet").use { stmt ->
            assertTrue(stmt.step())
            assertEquals(1, stmt.getLong(0))
        }
        connection.close()
    }

    @Test
    fun incorrectKey() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.deleteDatabase("encrypted.db")
        
        val connection =
            SQLCipherDriver(onPassphrase = { "key1" })
                .open(appContext.getDatabasePath("encrypted.db").absolutePath)
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS Pet
            (id INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY(id AUTOINCREMENT))
            """.trimIndent()
        )
        connection.execSQL("INSERT INTO Pet (name) VALUES ('Tom')")
        connection.close()

        assertThrows(SQLCipherInvalidKeyException::class.java) {
            SQLCipherDriver(onPassphrase = { "key2" })
                .open(appContext.getDatabasePath("encrypted.db").absolutePath)
        }
    }
}