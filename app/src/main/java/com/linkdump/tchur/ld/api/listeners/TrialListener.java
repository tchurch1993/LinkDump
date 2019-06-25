package com.linkdump.tchur.ld.api.listeners;

import com.google.android.gms.tasks.OnSuccessListener;
import com.linkdump.tchur.ld.objects.User;

public class TrialListener implements OnSuccessListener {


    @Override
    public void onSuccess(Object o) {

    }

    public User getResult() {

        return new User();
    }
}
