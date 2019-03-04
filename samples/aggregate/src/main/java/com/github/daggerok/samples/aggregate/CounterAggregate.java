package com.github.daggerok.samples.aggregate;

import com.github.daggerok.cqrses.aggregate.Aggregate;
import com.github.daggerok.cqrses.command.Command;
import com.github.daggerok.cqrses.event.Event;
import com.github.daggerok.samples.aggregate.command.CreateCounter;
import com.github.daggerok.samples.aggregate.command.DecrementCounter;
import com.github.daggerok.samples.aggregate.command.IncrementCounter;
import com.github.daggerok.samples.aggregate.event.CounterCreated;
import com.github.daggerok.samples.aggregate.event.CounterDecremented;
import com.github.daggerok.samples.aggregate.event.CounterIncremented;
import io.vavr.API;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Getter
@ToString
@RequiredArgsConstructor
public class CounterAggregate implements Aggregate {

    final UUID aggregateId;

    private long counter;

    @Override
    public Event handle(Command command) {
        return API.Match(command).of(
                Case($(instanceOf(CreateCounter.class)),
                     cmd -> new CounterCreated(cmd.getAggregateId())),
                Case($(instanceOf(IncrementCounter.class)),
                     cmd -> new CounterIncremented(cmd.getAggregateId())),
                Case($(instanceOf(DecrementCounter.class)),
                     cmd -> new CounterDecremented(cmd.getAggregateId())),
                Case($(), cmd -> Event.INVALID)
        );
    }

    @Override
    public Aggregate apply(Event event) {
        return API.Match(event).of(
                Case($(instanceOf(CounterCreated.class)),
                     evt -> new CounterAggregate(evt.getAggregateId())),
                Case($(instanceOf(CounterIncremented.class)), evt -> {
                    counter += 1;
                    return this;
                }),
                Case($(instanceOf(CounterDecremented.class)), evt -> {
                    counter -= 1;
                    return this;
                }),
                Case($(), () -> this)
        );
    }
}
