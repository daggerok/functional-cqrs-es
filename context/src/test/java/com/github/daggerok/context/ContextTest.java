package com.github.daggerok.context;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContextTest {

    Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Test
    void getContext() {
        Context context1 = Context.getContext();
        Context context2 = Context.getContext();
        assertThat(context1).isEqualTo(context2)
                            .isNotEqualTo(context);
    }

    @Test
    void register() {
        String string = context.register("");
        assertThat(string).isNotNull();
        assertThat(string == context.get(String.class.getName())).isTrue();
    }

    @Test
    void register1() {
        String string = context.register("aString", "");
        assertThat(context.get(String.class)).isNull();
        assertThat(context.has("aString")).isTrue();
        assertThat(context.has("aString", String.class)).isTrue();
        assertThat(string == context.get("aString", String.class)).isTrue();
    }

    @Test
    void register2() {
        val ref = new ParameterizedTypeReference<Collection<String>>() {};
        Collection<String> collection = context.register(ref, new ArrayList<>());
        assertThat(context.has(List.class)).isFalse();
        assertThat(context.has(ArrayList.class)).isFalse();
        assertThat(context.has(Collection.class)).isFalse();
        assertThat(context.has(ref)).isTrue();
        assertThat(context.get(ref).getClass()).isEqualTo(ArrayList.class);
        assertThat(collection == context.get(ref)).isTrue();
    }

    @Test
    void register3() {
        Collection<String> collection = context.register(new ArrayList<>());
        assertThat(context.has(ArrayList.class)).isTrue();
        assertThat(collection == context.get(ArrayList.class)).isTrue();
    }

    @Test
    void register4() {
        Serializable o = context.register("ref", String::new);
        assertThat(o == context.get("ref")).isTrue();
    }

    @Test
    void register5() {
        StringBuilder builder = new StringBuilder("ololo").append("trololo");
        StringBuilder stringBuilder = context.register(() -> builder);
        assertThat(stringBuilder == builder).isTrue();
    }

    @Test
    void register6() {
        context.register(Queue.class, new LinkedBlockingQueue<String>());
        assertThat(context.get(LinkedBlockingQueue.class)).isNotNull();
        assertThat(context.get(Queue.class)).isNotNull();
        assertThat(context.get(Queue.class) == context.get(LinkedBlockingQueue.class)).isTrue();
    }

    @Test
    void has() {
        Object aNull = context.register("not-null", "null");
        assertThat(context.has("not-null")).isTrue();
    }

    @Test
    void has1() {
        val ref = new ParameterizedTypeReference<Object>() {};
        Object o = ref;
        Object registered = context.register(ref, o);
        assertThat(registered == o).isTrue();
        assertThat(context.has(ref)).isTrue();
        assertThat(o == context.get(ref)).isTrue();
    }

    @Test
    void maybe() {
        val ref = new ParameterizedTypeReference<Object>() {};
        context.register(ref, new Object());
        assertThat(context.maybe(ref).isPresent()).isTrue();
        assertThat(context.maybe("ololo").isPresent()).isFalse();
    }

    @Test
    void maybe1() {
        assertThat(context.maybe(Object.class).isPresent()).isFalse();
        assertThatThrownBy(() -> context.maybe(List.class).get(),
                           "should throw on empty optional get");
    }

    @Test
    void maybe2() {
        assertThat(context.maybe("ololo", String.class).isPresent()).isFalse();
        String registered = context.register("trololo", "hohoho");
        assertThat(context.maybe("trololo", String.class).isPresent()).isTrue();
        assertThat(registered == context.maybe("trololo").get()).isTrue();
    }

    @Test
    void get() {
        String string = context.register("");
        assertThat(string == context.get(String.class)).isTrue();
    }

    @Test
    void get1() {
        String string = context.register("");
        String aString = context.get(String.class.getName());
        assertThat(aString == string).isTrue();
    }

    @Test
    void andRegister() {
        context.andRegister(ctx -> new HashSet<>(asList("ololo")))
               .andRegister(ctx -> new ArrayList<>(ctx.get(HashSet.class)));

        HashSet<String> hashSet = context.get(HashSet.class);
        ArrayList<String> arrayList = context.get(ArrayList.class);

        assertThat(arrayList).containsAll(new ArrayList<>(hashSet));
    }

    @Test
    void andRegister1() {
        context.andRegister("map", ctx -> new HashSet<>(asList("ololo")));
        context.andRegister("list", ctx -> new ArrayList<String>(ctx.get("map", HashSet.class)));

        HashSet<String> hashSet = context.get("map");
        List<String> list = context.get("list");
        assertThat(hashSet).containsAll(list);
    }

    @Test
    void andRegister2() {
        this.context.andRegister(Queue.class, ctx -> new LinkedBlockingQueue<String>());
        assertThat(context.has(LinkedBlockingQueue.class)).isTrue();
        assertThat(context.has(Queue.class)).isTrue();
        assertThat(context.get(Queue.class) == context.get(LinkedBlockingQueue.class)).isTrue();
    }

    @Test
    void andRegister3() {
        this.context.andRegister("queue", ctx -> new LinkedBlockingQueue<String>());
        assertThat(context.has("queue")).isTrue();
        Queue<String> queue = context.get("queue");
        assertThat(queue instanceof LinkedBlockingQueue).isTrue();
    }

    @Test
    void andRegister4() {
        val refIn = new ParameterizedTypeReference<HashSet<String>>() {};
        context.register(refIn, new HashSet<>(asList("ololo")));

        val refOut = new ParameterizedTypeReference<List<String>>() {};
        context.andRegister(refOut, ctx -> new ArrayList<>(ctx.get(refIn)));

        HashSet<String> hashSet = context.get(refIn);
        List<String> list = context.get(refOut);
        assertThat(hashSet).containsAll(list);
    }
}
