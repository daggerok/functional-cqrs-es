package com.github.daggerok.cqrses.aggregate;

import com.github.daggerok.cqrses.command.Command;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Value
@Getter(PRIVATE)
public class AggregateRoot {

    private final AggregateRegisterer aggregateRegisterer;
    private final AggregateRepository aggregateRepository;

    /* Each Aggregate has a constructor with single UUID aggregateId argument */
    public <C extends Command, A extends Aggregate> Optional<A> createNewAggregateIfNeededFor(C maybeInitialCommand) {
        if (find(maybeInitialCommand.getAggregateId()).isPresent()) return Optional.empty(); // aggregate exists
        Optional<A> maybe = Option.ofOptional(aggregateRegisterer.findRegistrationFor(maybeInitialCommand))
                                  .map(a -> Try.of(() -> a.getConstructor(UUID.class))
                                               .getOrElseGet(throwable -> null))
                                  .filter(Objects::nonNull)
                                  .map(c -> Try.of(() -> c.newInstance(maybeInitialCommand.getAggregateId()))
                                               .getOrElseGet(throwable -> null))
                                  .filter(Objects::nonNull)
                                  .map(o -> (A) o)
                                  .toJavaOptional();
        maybe.ifPresent(aggregateRepository::save);
        return maybe;
    }

    public void save(Aggregate aggregate) {
        log.info(aggregate.toString());
        aggregateRepository.save(aggregate);
    }

    public Optional<Aggregate> find(UUID aggregateId) {
        return aggregateRepository.findAggregate(aggregateId);
    }
}
