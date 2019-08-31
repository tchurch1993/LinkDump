package com.linkdump.tchur.ld.interfaces

interface IFirebaseDbContext {
    fun findById()
    fun delete()
    fun update()
    fun create()
}
