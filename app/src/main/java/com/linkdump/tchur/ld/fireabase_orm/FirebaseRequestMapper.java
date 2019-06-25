package com.linkdump.tchur.ld.fireabase_orm;


/*

   this class is going to map request object and/or

 */


public class FirebaseRequestMapper<T> {

    private T result;

    private FirebaseRequestMapper(){

    }

    public static FirebaseRequestMapper MapToFormat() {
        return new FirebaseRequestMapper();
    }


    public T getResult(){


        return result;
    }


}


