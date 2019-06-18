package com.linkdump.tchur.ld.handlers;



import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.linkdump.tchur.ld.IHandleCanceled;
import com.linkdump.tchur.ld.IHandleCompleted;
import com.linkdump.tchur.ld.IHandleFailure;
import com.linkdump.tchur.ld.IHandleSuccess;


import java.util.function.Function;

public class FirebaseTaskHandler {

      private Task task;

      private IHandleCanceled canceledHandler;
      private IHandleCompleted completedHandler;
      private IHandleFailure failureHandler;
      private IHandleSuccess successHandler;



      public FirebaseTaskHandler(Task task){
             this.task = task;
      }




      public void Execute()
      {
          task.addOnSuccessListener(o -> { successHandler.onSuccess(); })
              .addOnCompleteListener(task -> { completedHandler.onComplete();})
              .addOnFailureListener(e -> { failureHandler.onFailure(); })
              .addOnCanceledListener(() -> { canceledHandler.onCanceled();});
      }


}
