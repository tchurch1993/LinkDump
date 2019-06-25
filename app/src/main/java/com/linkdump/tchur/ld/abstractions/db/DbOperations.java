package com.linkdump.tchur.ld.abstractions.db;

import com.google.firebase.database.FirebaseDatabase;

public interface DbOperations {
      public DbOperations create(FirebaseDatabase db);
      public DbOperations read(FirebaseDatabase db);
      public DbOperations update(FirebaseDatabase db);
      public DbOperations delete(FirebaseDatabase db);
}
