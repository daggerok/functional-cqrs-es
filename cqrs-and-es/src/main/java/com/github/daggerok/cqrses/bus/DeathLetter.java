package com.github.daggerok.cqrses.bus;

import org.greenrobot.eventbus.util.ThrowableFailureEvent;

public class DeathLetter extends ThrowableFailureEvent {
    public DeathLetter(Throwable throwable) {
        super(throwable);
        System.out.println("oops: " + throwable.getLocalizedMessage());
    }
}
