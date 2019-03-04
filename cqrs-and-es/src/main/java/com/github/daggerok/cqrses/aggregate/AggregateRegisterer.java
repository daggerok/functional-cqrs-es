package com.github.daggerok.cqrses.aggregate;

import com.github.daggerok.cqrses.command.Command;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.Getter;
import lombok.Value;

import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Value
@Getter(PRIVATE)
public class AggregateRegisterer {

    private final Map<Class, Class> registry;

    public void registerCreator(Class<?> aggregate, Class<?> createBy) {
        registry.put(aggregate, createBy);
    }

    public Optional<Class> findRegistrationFor(Command maybeInitialCommand) {
        return HashMap.ofAll(registry)
                      .filter((a, c) -> c.equals(maybeInitialCommand.getClass()))
                      .map(Tuple2::_1)
                      .headOption()
                      .toJavaOptional();
    }
}
