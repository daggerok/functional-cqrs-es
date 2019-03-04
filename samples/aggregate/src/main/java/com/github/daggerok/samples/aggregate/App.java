package com.github.daggerok.samples.aggregate;

import com.github.daggerok.context.Context;
import com.github.daggerok.cqrses.Configurer;
import com.github.daggerok.cqrses.aggregate.AggregateRegisterer;
import com.github.daggerok.cqrses.command.CommandGateway;
import com.github.daggerok.samples.aggregate.command.CreateCounter;
import com.github.daggerok.samples.aggregate.command.DecrementCounter;
import com.github.daggerok.samples.aggregate.command.IncrementCounter;

import java.util.UUID;

public class App {
    public static void main(String[] args) {
        Configurer.initializeContext();

        Context.getContext()
               .get(AggregateRegisterer.class)
               .registerCreator(CounterAggregate.class, CreateCounter.class);

        CommandGateway commandGateway = Context.getContext().get(CommandGateway.class);
        UUID id = UUID.randomUUID();
        commandGateway.send(() -> new CreateCounter(id));
        commandGateway.send(() -> new IncrementCounter(id));
        commandGateway.send(() -> new IncrementCounter(id));
        commandGateway.send(() -> new DecrementCounter(id));
        commandGateway.send(() -> new IncrementCounter(id));
    }
}
