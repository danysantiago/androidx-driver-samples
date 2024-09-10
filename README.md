## androidx SQLite Driver Samples

A small collection of `androidx.sqlite`
[driver](https://developer.android.com/kotlin/multiplatform/sqlite) implementations for
demonstration purposes along with a sample app using
[Room](https://developer.android.com/jetpack/androidx/releases/room) along with the drivers.

### Logging Driver

The `logging-driver` module contains a driver implementation that will invoke a given callback
function when a statement is prepared, which can be used for logging.
```kotlin
val myDriver = baseDriver.withLogging { id, sql ->
  Log.i("Sample App", "Connection #$id executing SQL: $sql")
}
```

### Copy Driver

The `copy-driver` module contains a driver implementation that first copies another database file
to the location where the driver is directed to open a database connection such that a pre-created /
pre-packaged database is opened instead. For example, it could be used by apps to open downloaded
databases, or databases included in the APK assets.
```kotlin
val myDriver = baseDriver.withCopyFrom {
  context.assets.open("book.db")
}
```
### SQLCipher Driver

The `sqlcipher-driver` module contains a driver implementation that uses
[SQLCipher](https://github.com/sqlcipher/sqlcipher) and thus enables the database to be encrypted.
It is the same as the `BundledSQLiteDriver` available in `androidx.sqlite:sqlite-bundled` but linked
against a SQLCipher compiled from source and a pre-compiled `libcrypto` from
[OpenSSL](https://github.com/openssl/openssl).
```kotlin
val myDriver = SQLCipherDriver(
  onPassphrase = { passphrase }
)
```

### Sample Android Application

The `sample-app` module contains a sample Android application using all 3 drivers at the same time.
It showcases the ability for Room to use these drivers to extend its functionality.