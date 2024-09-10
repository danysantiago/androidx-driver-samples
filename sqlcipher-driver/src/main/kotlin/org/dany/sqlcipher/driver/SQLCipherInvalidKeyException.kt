package org.dany.sqlcipher.driver

/**
 * An exception thrown by [SQLCipherDriver] when opening a connection whose passphrase is incorrect.
 */
class SQLCipherInvalidKeyException : Throwable()