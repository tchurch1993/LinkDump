package com.linkdump.tchur.ld.adapters;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.MenuItem;

import com.linkdump.tchur.ld.abstractions.IIntentAdapter;

import java.lang.reflect.Type;
import java.util.function.Function;

//TODO: Explain to me wtf this does
public class IntentAdapter<T> implements IIntentAdapter {



     private Function<T, String> onCompleted;
     private Function<Exception, String> onException;
     private Function<T, String> onNext;

     private String serializable;
     private Intent intent;
     private T result;



     private IntentAdapter(){

     }



     public IntentAdapter SetIntent(Intent intent){
           this.intent = intent;
           return this;
     }



     public IntentAdapter SetOnNext(Function<T, String> onNextListener){
          this.onNext = onNextListener;
          return this;
     }




     public IntentAdapter SetOnCompleted(Function<T, String> onCompletedListener)
     {
          this.onCompleted = onCompletedListener;
          return this;
     }




     public IntentAdapter SetSerializable(String serializable)
     {
          this.serializable = serializable;
          return this;
     }




     public IntentAdapter SetOnException(Function<Exception, String> exceptionListener){
          this.onException = exceptionListener;
          return this;
     }


     public IntentAdapter GetFromExtra(T t, Function<Intent, T> func)
     {
          t = func.apply(intent);
          return this;
     }

     public static IntentAdapter Begin()
     {
          return new IntentAdapter();
     }


     public String Serialize()
     {

          try
          {
               onCompleted.apply(result);
          }
          catch (Exception ex)
          {
               onException.apply(ex);
          }
          return serializable;
     }


     public T Deserialize()
     {
          try
          {
               onCompleted.apply(result);
          }
          catch (Exception ex){
               onException.apply(ex);
          }
          return result;
     }


}
