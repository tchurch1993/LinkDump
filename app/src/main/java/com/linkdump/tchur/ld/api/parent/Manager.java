package com.linkdump.tchur.ld.api.parent;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.linkdump.tchur.ld.abstractions.Managers.IManager;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;

import java.util.function.Predicate;

public class Manager implements IManager {


    protected FirebaseDbContext firebaseDbContext;

    protected OnSuccessListener successListener;
    protected OnFailureListener failureListener;
    protected OnCanceledListener canceledListener;

    protected Predicate where;

    protected int queryLimit;


    public Manager(FirebaseDbContext db) {
        this.firebaseDbContext = db;
    }


    public Manager OnSuccess(OnSuccessListener onSuccessListener) {
        this.successListener = onSuccessListener;
        return this;
    }


    public Manager OnFailure(OnFailureListener onFailureListener) {
        this.failureListener = onFailureListener;
        return this;
    }

    public Manager OnCanceled(OnCanceledListener onCanceledListener) {
        this.canceledListener = onCanceledListener;
        return this;
    }

    public Manager Where(Predicate where) {
        this.where = where;
        return this;
    }


    @Override
    public void initialiseManager(Runnable runner) {

    }


}
