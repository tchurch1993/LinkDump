package com.linkdump.tchur.ld.handlers;



import com.google.android.gms.tasks.Task;
import com.linkdump.tchur.ld.abstractions.eventbased.IHandleCanceled;
import com.linkdump.tchur.ld.abstractions.eventbased.IHandleCompleted;
import com.linkdump.tchur.ld.abstractions.eventbased.IHandleFailure;
import com.linkdump.tchur.ld.abstractions.eventbased.IHandleSuccess;

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
