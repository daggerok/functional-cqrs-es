package com.github.daggerok.cqrses;

import com.github.daggerok.context.Context;
import com.github.daggerok.cqrses.aggregate.Aggregate;
import com.github.daggerok.cqrses.aggregate.AggregateRegisterer;
import com.github.daggerok.cqrses.aggregate.AggregateRepository;
import com.github.daggerok.cqrses.aggregate.AggregateRoot;
import com.github.daggerok.cqrses.command.CommandGateway;
import com.github.daggerok.cqrses.command.CommandHandler;
import com.github.daggerok.cqrses.event.Event;
import com.github.daggerok.cqrses.event.EventHandler;
import com.github.daggerok.cqrses.event.EventStore;
import com.github.daggerok.cqrses.event.EventStoreHandler;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static lombok.AccessLevel.PRIVATE;

// CommandGateway sent Command forward ->
// -> CommandHandler registered new or handled it on existed Aggregate with AggregateRoot and result Event fired ->
// -> -> EventHandler added new Event to EventStore and fired EventSaved Event forward ->
// -> -> -> EventStoreHandler fetched Aggregate from AggregateRoot and EventSaved Event payload applied to Aggregate
@NoArgsConstructor(access = PRIVATE)
public class Configurer {

    public static Context initializeContext() {
        return Context.getContext()
                      // aggregates
                      .andRegister("aggregateRepositoryRegistry",
                                   context -> new ConcurrentHashMap<Class, Class>())
                      .andRegister(AggregateRegisterer.class,
                                   context -> new AggregateRegisterer(context.get("aggregateRepositoryRegistry")))
                      .andRegister("aggregateRepositoryStore",
                                   context -> new ConcurrentHashMap<UUID, Aggregate>())
                      .andRegister(AggregateRepository.class,
                                   context -> new AggregateRepository(context.get("aggregateRepositoryStore")))
                      .andRegister(AggregateRoot.class,
                                   context -> new AggregateRoot(context.get(AggregateRegisterer.class),
                                                                context.get(AggregateRepository.class)))
                      // commands
                      .andRegister(new CommandGateway())
                      .andRegister(context -> new CommandHandler(context.get(AggregateRoot.class)))
                      // events
                      .andRegister("eventStoreRepository",
                                   context -> new ConcurrentHashMap<UUID, CopyOnWriteArrayList<Event>>())
                      .andRegister(EventStore.class, context -> new EventStore(context.get("eventStoreRepository")))
                      .andRegister(EventHandler.class, context -> new EventHandler(context.get(EventStore.class)))
                      .andRegister(EventStoreHandler.class,
                                   context -> new EventStoreHandler(context.get(AggregateRoot.class)))
                ;
    }

    public static void main(String[] args) {
        initializeContext();
    }
}
