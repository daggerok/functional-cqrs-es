package com.github.daggerok.cqrses.bus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.util.AsyncExecutor;

import java.util.function.Supplier;

public interface Bus {

    EventBus eventBus = EventBus.builder()
                                .throwSubscriberException(true)
                                .installDefaultEventBus();

    default void subscribe() {
        eventBus.register(this);
    }

    default <T> void sync(Supplier<T> supplier) {
        eventBus.post(supplier.get());
    }

    AsyncExecutor asyncExecutor = AsyncExecutor.builder()
                                               .eventBus(eventBus)
                                               .failureEventType(DeathLetter.class)
                                               .build();

    default <T> void async(Supplier<T> supplier) {
        asyncExecutor.execute(() -> eventBus.post(supplier.get()));
    }
}
