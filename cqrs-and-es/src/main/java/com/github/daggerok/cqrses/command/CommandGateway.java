package com.github.daggerok.cqrses.command;

import com.github.daggerok.cqrses.bus.Bus;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class CommandGateway implements Bus {

    public <C extends Command> void send(Supplier<C> commander) {
        sync(() -> {
            C command = commander.get();
            log.debug("Sending command {}", command);
            return command;
        });
    }
}
