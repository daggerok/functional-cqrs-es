package com.github.daggerok.samples.aggregate.command;

import com.github.daggerok.cqrses.command.Command;
import lombok.Value;

import java.util.UUID;

@Value
public class IncrementCounter implements Command {
    final UUID aggregateId;
}
