cmake_minimum_required(VERSION 3.22.1)

project("sqlcipher")

# SQLCipher amalgamation source and header created from https://github.com/sqlcipher/sqlcipher
# See instructions at https://www.sqlite.org/amalgamation.html#building_the_amalgamation_from_canonical_source_code
add_library(sqlcipher SHARED "sqlite3.c")
target_include_directories(sqlcipher PRIVATE "libs/include")

target_compile_options(
        sqlcipher
        PRIVATE
        -DHAVE_USLEEP=1
        -DSQLCIPHER_CRYPTO_OPENSSL
        -DSQLITE_DEFAULT_AUTOVACUUM=1
        -DSQLITE_DEFAULT_MEMSTATUS=0
        -DSQLITE_DEFAULT_WAL_SYNCHRONOUS=1
        -DSQLITE_ENABLE_COLUMN_METADATA
        -DSQLITE_ENABLE_FTS3
        -DSQLITE_ENABLE_FTS3_PARENTHESIS
        -DSQLITE_ENABLE_FTS4
        -DSQLITE_ENABLE_FTS5
        -DSQLITE_ENABLE_JSON1
        -DSQLITE_ENABLE_NORMALIZE
        -DSQLITE_ENABLE_RTREE
        -DSQLITE_ENABLE_STAT4
        -DSQLITE_HAS_CODEC
        -DSQLITE_HAVE_ISNAN
        -DSQLITE_OMIT_BUILTIN_TEST
        -DSQLITE_OMIT_DEPRECATED
        -DSQLITE_OMIT_PROGRESS_CALLBACK
        -DSQLITE_OMIT_SHARED_CACHE
        -DSQLITE_SECURE_DELETE
        -DSQLITE_TEMP_STORE=3
        -DSQLITE_THREADSAFE=2
)

# Crypto library and headers obtained from https://github.com/openssl/openssl and included as object
# See instructions at https://github.com/openssl/openssl/blob/master/NOTES-ANDROID.md, but also
# more easily obtainable from https://github.com/areful/NdkUseOpenssl and
# https://github.com/217heidai/openssl_for_android
add_library(crypto SHARED IMPORTED)
set_target_properties(crypto PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI}/libcrypto.so)

find_library(log log)

target_link_libraries(sqlcipher crypto log)
