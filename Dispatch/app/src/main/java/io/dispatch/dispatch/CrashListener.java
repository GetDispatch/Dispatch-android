package io.dispatch.dispatch;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel on 5/16/2015.
 */
public abstract class CrashListener implements Serializable {
    private String message;
    private final List<Contact> contacts;

    public CrashListener(String message, final List<Contact> contacts) {
        this.message = message;
        this.contacts = contacts;
    }

    public abstract void onPossibleCrash(CrashService service, Context context);

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public String getMessage() {
        return message;
    }
}
