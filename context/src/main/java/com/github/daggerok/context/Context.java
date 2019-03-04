package com.github.daggerok.context;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PACKAGE)
public class Context {

    @NoArgsConstructor(access = PRIVATE)
    private static class ContextFactory {
        private static Context INSTANCE = new Context();
    }

    public static Context getContext() {
        return ContextFactory.INSTANCE;
    }

    private final Map<String, Object> context = new ConcurrentHashMap<>();

    /***/

    public <T, REF extends ParameterizedTypeReference<T>> Context andRegister(REF ref, Function<Context, T> producer) {
        Objects.requireNonNull(producer, "producer argument required");
        T bean = producer.apply(this);
        register(ref, bean);
        return this;
    }

    public <T> Context andRegister(String name, Function<Context, T> producer) {
        Objects.requireNonNull(producer, "producer argument required");
        T bean = producer.apply(this);
        register(name, bean);
        return this;
    }

    public <AS, T extends AS> Context andRegister(Class<AS> type, Function<Context, T> producer) {
        Objects.requireNonNull(type, "type argument required");
        T bean = producer.apply(this);
        if (!register(bean).getClass().equals(type))
            register(type.getName(), bean);
        return this;
    }

    public <T> Context andRegister(Function<Context, T> producer) {
        Objects.requireNonNull(producer, "producer argument required");
        T bean = producer.apply(this);
        T registered = register(bean);
        return this;
    }

    public <T> Context andRegister(T bean) {
        Objects.requireNonNull(bean, "bean argument required");
        register(bean);
        return this;
    }

    /***/

    public <T> T register(Supplier<T> definition) {
        T bean = Objects.requireNonNull(definition, "definition argument required")
                        .get();
        return register(bean);
    }

    public <T> T register(String name, Supplier<T> definition) {
        Objects.requireNonNull(name, "name argument required");
        T bean = Objects.requireNonNull(definition, "definition argument required")
                        .get();
        return register(name, bean);
    }

    /***/

    public <T> T register(T bean) {
        Class<?> type = Objects.requireNonNull(bean, "bean argument required")
                               .getClass();
        return register(type.getName(), bean);
    }

    public <AS, T extends AS> AS register(Class<AS> type, T bean) {
        String parent = Objects.requireNonNull(type, "type argument required")
                               .getName();
        AS registered = register(parent, bean);
        String child = Objects.requireNonNull(bean, "bean argument required")
                              .getClass()
                              .getName();
        if (parent.equals(child)) return registered;
        return register(child, bean);
    }

    public <T> T register(String name, T bean) {
        Objects.requireNonNull(name, "name argument required");
        Objects.requireNonNull(bean, "bean argument required");
        context.put(name, bean);
        return bean;
    }

    public <T, REF extends ParameterizedTypeReference<T>> T register(REF ref, T bean) {
        Type type = Objects.requireNonNull(ref, "ref argument required")
                           .getType();
        Objects.requireNonNull(bean, "bean argument required");
        return register(type.getTypeName(), bean);
    }

    /***/

    public <T, REF extends ParameterizedTypeReference<T>> boolean has(REF ref) {
        Type type = Objects.requireNonNull(ref, "ref argument required")
                           .getType();
        return has(type.getTypeName());
    }

    public <T> boolean has(Class<T> type) {
        String name = Objects.requireNonNull(type, "type argument required")
                             .getName();
        return has(name, type);
    }

    public <T> boolean has(String name, Class<T> type) {
        Objects.requireNonNull(name, "name argument required");
        Objects.requireNonNull(type, "type argument required");
        Object o = context.get(name);
        return Objects.nonNull(o) && type.isInstance(o);
    }

    public boolean has(String name) {
        Objects.requireNonNull(name, "name argument required");
        Object o = context.get(name);
        return Objects.nonNull(o);
    }

    /***/

    public <T> Optional<T> maybe(String name) {
        return Optional.ofNullable(get(name));
    }

    public <T> Optional<T> maybe(Class<T> type) {
        return Optional.ofNullable(get(type));
    }

    public <T> Optional<T> maybe(String name, Class<T> type) {
        return Optional.ofNullable(get(name, type));
    }

    public <T, REF extends ParameterizedTypeReference<T>> Optional<T> maybe(REF ref) {
        Type type = Objects.requireNonNull(ref, "ref argument required")
                           .getType();
        return Optional.ofNullable(get(type.getTypeName()));
    }

    /***/

    public <T, REF extends ParameterizedTypeReference<T>> T get(REF ref) {
        Type type = Objects.requireNonNull(ref, "ref argument required")
                           .getType();
        return get(type.getTypeName());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        Objects.requireNonNull(name, "name argument required");
        Object o = context.get(name);
        return Try.of(() -> (T) o)
                  .getOrElseGet(throwable -> null);
    }

    public <T> T get(Class<T> type) {
        String name = Objects.requireNonNull(type, "type argument required")
                             .getName();
        return get(name, type);
    }

    public <T> T get(String name, Class<T> type) {
        Objects.requireNonNull(name, "name argument required");
        Objects.requireNonNull(type, "type argument required");
        Object o = context.get(name);
        return type.isInstance(o) ? type.cast(o) : null;
    }
}
