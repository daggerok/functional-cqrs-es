# functional CQRS / event sourcing
Functional (not production ready at all!) library to build simple EventSourcing app

- Travis CI [![Build Status](https://travis-ci.org/daggerok/functional-cqrs-es.svg?branch=master)](https://travis-ci.org/daggerok/functional-cqrs-es)
- GitHub [Pages](https://daggerok.github.io/functional-cqrs-es/) documentation
- GitHub [daggerok/functional-cqrs-es](https://github.com/daggerok/functional-cqrs-es) repository 

## TODO

- _Implement_ `Command`(s) and `Event`(s)
- _Implement_ `Aggregate`
- _Override_ `handle` and `on` methods og the `Aggregate`
- _Bootstrap_ application context with `Configurer`
- _Register_ creation `Command` for an `Aggregate`

::: tip
See `samples/aggregate` example project for details
:::

<!--

_Aggregate implementation sample_

```java
public interface Aggregate<ID> {
    ID getAggregateId();
}
```

_Aggregate implementation sample_

```java
class MyAggregate implements Aggregate<UUID> {

    @Getter final UUID aggregateId;

    public MyAggregate(UUID aggregateId) {
        this.aggregateId = aggregateId;
    }
}
```

-->

## build

```bash
./gradlew
```

## increment version

```bash
./gradlew incrementVersion
```

## build GitHub Pages VuePress documentation

```bash
./gradlew npm_run_build
```
