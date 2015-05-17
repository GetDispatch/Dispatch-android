package io.dispatch.dispatch;

import java.io.Serializable;

/**
 * Created by Daniel on 5/16/2015.
 */
public class Contact implements Serializable {
    private final String number;

    public Contact(final String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }


    @Override
    public String toString() {
        return number + "";
    }
}
