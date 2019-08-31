package com.linkdump.tchur.ld.interfaces

interface IDbSet {
    fun findById()
    fun delete()
    fun update()
    fun create()
}