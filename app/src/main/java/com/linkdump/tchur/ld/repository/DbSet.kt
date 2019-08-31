package com.linkdump.tchur.ld.repository

import com.linkdump.tchur.ld.interfaces.IDbSet


/*

    Tylor:
      This class is just a wrapper around the document references inside of the firesbase library
      1. I do this because i can set convenience functions inside here
      2. By wrapping this class like this, we can keep the application db agnostic, so if in the future we ever needed to use a different service, boom
      3. this also replicates the famous repository pattern and allows us to pass DbSet instead of being stuck with DocumentReference Object
      (Side note, this class is placed in the main db context)


 */
class DbSet : IDbSet {


    override fun create() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}