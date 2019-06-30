package com.linkdump.tchur.ld.abstractions.listeners;

import com.google.firebase.firestore.DocumentChange;

public interface OnDocumentChangedListener {
    void OnDocumentChanged(DocumentChange change);
}
