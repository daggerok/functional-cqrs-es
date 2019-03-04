package com.github.daggerok.cqrses.aggregate;

import com.github.daggerok.cqrses.command.Command;
import com.github.daggerok.cqrses.event.Event;
import io.vavr.API;

import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;

public interface Aggregate {

    UUID getAggregateId();

    /**
     * This is how you can handle {@link Command} by using pattern matching
     * with vavr.io library. Here you must verify if received command is valid.
     *
     * @param command {@link Command} to be handled by this {@link Aggregate}
     * @return {@link Event} to be placed into EventStore apply valid {@link Command}
     */
    default Event handle(Command command) {
        return API.Match(command).of(
                //Case($(instanceOf(SomeCommand.class)), cmd -> new SomeEvent(cmd.getAggregateId())),
                Case($(), cmd -> Event.INVALID)
                //Case($(), o -> null) // will throw here with NPE.
                //Should throw MatchError if something not defined was received.
        );
    }

    /**
     * This is how you can handle {@link Event} by using pattern matching
     * with vavr.io library. Here you should change your aggregate state.
     *
     * @param event {@link Event} to be applied on this {@link Aggregate}
     * @return New {@link Aggregate} state according to applied Event
     */
    default Aggregate apply(Event event) {
        return API.Match(event).of(
                //Case($(instanceOf(SomeEvent.class)), evt -> { this.counter++; return this; }),
                Case($(), () -> this)
                //Case($(), o -> null) // will throw here with NPE.
                //It Should throw MatchError when matching something not defined inside Case.
        );
    }
}
