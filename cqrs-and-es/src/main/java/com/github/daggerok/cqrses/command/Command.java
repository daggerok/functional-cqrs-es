package com.github.daggerok.cqrses.command;

import java.util.UUID;

public interface Command {
    UUID getAggregateId();
}
