cmake_minimum_required(VERSION 3.22.1)

project("SQLiteJni")

add_subdirectory("sqlcipher/")

add_library(sqliteJni SHARED "sqlite_bindings.cpp")
target_include_directories(sqliteJni PRIVATE "sqlcipher/")

target_link_libraries(sqliteJni sqlcipher)
target_link_options(sqliteJni PRIVATE "-Wl,-z,max-page-size=16384")
