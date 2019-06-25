package com.linkdump.tchur.ld.fireabase_orm;


import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Field;

/*

    TYLOR THIS CLASS IS SUPER CRUCIAL
    -1 IT PREVENTS YOU HAVING TO DUPLICATE THE SAME MAPPING PROCESS OVER AND OVER AGAIN
    -2 IF YOU NEED TO ASYNCHRONOUSLY MAP A BUNCH OF RESPONSES FROM FIREBASE, THIS WILL REDUCE THE BOILERPLATE CALLS BY 1000%
    -3 THIS WILL ALLOW THE EVENT HANDLERS TO GET PASSED A MAPPER INSTANCE AND KEEP YOU FROM HAVING TO REWRITE YOUR UI EVENT SPECIFIC STUFF (SMILEY FACE)
    TODO: TEACH ME WHAT THIS EVEN MEANS REEEEEEEEEEEEEEEEEEE

     -Tyler this means
      - that if you use reflection to dynamically map responses to a particular type, your in memory objects and your database objects will always match up
      - this is important because you may not always use firebase for your database, you want to keep your database and your program as decoupled as possible
*/
public class FirebaseResponseMapper<T> {




    private FirebaseResponseMapper(){

    }



    public FirebaseResponseMapper Map()
    {
          return new FirebaseResponseMapper();
    }




    public T MapToDto(DocumentSnapshot snapshot) {


       /* Object someObject = getItSomehow();

        for (Field field : someObject.getClass().getDeclaredFields()) {
            field.setAccessible(true); // You might want to set modifier to public first.
            Object value = null;
            try
            {
                value = field.get(someObject);
                if (value != null)
                {
                    System.out.println(field.getName() + "=" + value);
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

        }*/
       return null;
    }

}
