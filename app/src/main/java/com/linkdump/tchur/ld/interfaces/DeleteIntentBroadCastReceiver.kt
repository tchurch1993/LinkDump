package com.linkdump.tchur.ld.interfaces

import com.google.firebase.database.snapshot.Index

interface DeleteIntentBroadCastReceiver {
    fun deleteBroadCast(index: Index)

}