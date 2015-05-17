package io.dispatch.dispatch;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel on 5/16/2015.
 */
public abstract class CrashListener implements Serializable {
    private final String message;
    private final List<Contact> contacts;

    public CrashListener(final String message, final List<Contact> contacts) {
        this.message = message;
        this.contacts = contacts;
    }

    public abstract void onPossibleCrash(Context context);

    public List<Contact> getContacts() {
        return contacts;
    }

    public String getMessage() {
        return message;
    }
}
