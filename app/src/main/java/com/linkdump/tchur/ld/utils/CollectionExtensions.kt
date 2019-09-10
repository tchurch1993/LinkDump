package com.linkdump.tchur.ld.utils


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.function.Predicate


class CollectionExtensions {

    fun ArrayList<Any>.whereFirstOrDefault(predicate: Predicate<Any>): Any {
        var result: Any = this.first()
        for (i in this)
            if (predicate.test(i))
                result = i

        return result
    }


    suspend fun ArrayList<Any>.whereFirstOrDefaultAsync(predicate: Predicate<Any>): Any {

        val collection = this
        var result: Any = collection.first()
        val a = coroutineScope {
            launch(Dispatchers.Unconfined) {
                for (i in collection)
                    if (predicate.test(i))
                        result = i


            }
        }
        a.join()
        return result


    }
}
