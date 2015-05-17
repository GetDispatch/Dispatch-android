package io.dispatch.dispatch;

import java.util.List;

/**
 * Created by Daniel on 5/17/2015.
 */
public interface IContactListener {
    public void onContactsImported(List<Contact> contactList);
}
