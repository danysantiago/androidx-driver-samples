@file:JvmName("SQLCipher")

package org.dany.sqlcipher.driver

import androidx.annotation.IntDef

const val SQLITE_OPEN_READONLY: Int = 0x00000001
const val SQLITE_OPEN_READWRITE: Int = 0x00000002
const val SQLITE_OPEN_CREATE: Int = 0x00000004
const val SQLITE_OPEN_URI: Int = 0x00000040
const val SQLITE_OPEN_MEMORY: Int = 0x00000080
const val SQLITE_OPEN_NOMUTEX: Int = 0x00008000
const val SQLITE_OPEN_FULLMUTEX: Int = 0x00010000
const val SQLITE_OPEN_NOFOLLOW: Int = 0x01000000
const val SQLITE_OPEN_EXRESCODE: Int = 0x02000000

/** The flags constant that can be used with [SQLCipherDriver.open]. */
@IntDef(
    flag = true,
    value =
        [
            SQLITE_OPEN_READONLY,
            SQLITE_OPEN_READWRITE,
            SQLITE_OPEN_CREATE,
            SQLITE_OPEN_URI,
            SQLITE_OPEN_MEMORY,
            SQLITE_OPEN_NOMUTEX,
            SQLITE_OPEN_FULLMUTEX,
            SQLITE_OPEN_NOFOLLOW
        ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class OpenFlag

internal object ResultCode {
    const val SQLITE_MISUSE = 21
}
