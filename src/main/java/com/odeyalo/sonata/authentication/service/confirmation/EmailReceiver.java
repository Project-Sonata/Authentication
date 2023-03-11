package com.odeyalo.sonata.authentication.service.confirmation;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.Assert;

public record EmailReceiver(String address) {

    public EmailReceiver {
        Assert.notNull(address, "The address must be not null!");
        Assert.isTrue(EmailValidator.getInstance().isValid(address), "The address must be valid email address!");
    }

    public static EmailReceiver of(String address) {
        return new EmailReceiver(address);
    }

    @Override
    public String toString() {
        return "EmailReceiver{" +
                "address='" + address + '\'' +
                '}';
    }
}
