package com.holddie.reactor;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Learn how to create Flux instances.
 *
 * @author Sebastien Deleuze
 * @see <a
 *     href="https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html">Flux
 *     Javadoc</a>
 */
public class Part01FluxTest {

    Part01Flux workshop = new Part01Flux();

    // ========================================================================================

    @Test
    public void empty() {
        Flux<String> flux = workshop.emptyFlux();

        StepVerifier.create(flux).verifyComplete();
    }

    // ========================================================================================

    @Test
    public void fromValues() {
        Flux<String> flux = workshop.fooBarFluxFromValues();
        StepVerifier.create(flux).expectNext("foo", "bar").verifyComplete();
    }

    // ========================================================================================

    @Test
    public void fromList() {
        Flux<String> flux = workshop.fooBarFluxFromList();
        StepVerifier.create(flux).expectNext("foo", "bar").verifyComplete();
    }

    // ========================================================================================

    @Test
    public void error() {
        Flux<String> flux = workshop.errorFlux();
        StepVerifier.create(flux).verifyError(IllegalStateException.class);
    }

    // ========================================================================================

    @Test
    public void countEach100ms() {
        Flux<Long> flux = workshop.counter();
        StepVerifier.create(flux)
                .expectNext(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
                .verifyComplete();
    }

    @Test
    public void testBaseSubscriber() {
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);
    }

    @Test
    public void testSubscriber01() {
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe();
        ints.subscribe(System.out::println);
    }

    @Test
    public void testSubscriber02() {
        Flux<Integer> ints =
                Flux.range(1, 4)
                        .map(
                                i -> {
                                    if (i <= 3) return i;
                                    throw new RuntimeException("Got to 4");
                                });
        ints.subscribe(System.out::println, error -> System.err.println("Error: " + error));
    }

    @Test
    public void testSubscriber03() {
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(
                System.out::println,
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"));
    }

    @Test
    public void testSubscriber04() {
        Flux<Integer> ints = Flux.range(1, 14);
        ints.subscribe(
                System.out::println,
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"),
                sub -> sub.request(10));
    }

    @Test
    public void testSubscriber05() {
        Flux.range(1, 10)
                .doOnRequest(r -> System.out.println("request of " + r))
                .subscribe(
                        new BaseSubscriber<Integer>() {

                            @Override
                            public void hookOnSubscribe(Subscription subscription) {
                                request(1);
                            }

                            @Override
                            public void hookOnNext(Integer integer) {
                                System.out.println("Cancelling after having received " + integer);
                                //                        request(1);
                                cancel();
                            }
                        });
    }

    @Test
    public void testSubscriber06() {
        Flux<String> flux =
                Flux.generate(
                        () -> 0,
                        (state, sink) -> {
                            sink.next("3 x " + state + " = " + 3 * state);
                            if (state == 10) sink.complete();
                            return state + 1;
                        });
        flux.subscribe(System.out::println);
        System.out.println("-----------------------------");
        flux =
                Flux.generate(
                        AtomicLong::new,
                        (state, sink) -> {
                            long i = state.getAndIncrement();
                            sink.next("3 x " + i + " = " + 3 * i);
                            if (i == 10) sink.complete();
                            return state;
                        });
        flux.subscribe(System.out::println);
        System.out.println("-----------------------------");
        flux =
                Flux.generate(
                        AtomicLong::new,
                        (state, sink) -> {
                            long i = state.getAndIncrement();
                            sink.next("3 x " + i + " = " + 3 * i);
                            if (i == 10) sink.complete();
                            return state;
                        },
                        (state) -> System.out.println("state: " + state));
        flux.subscribe(System.out::println);
    }

    @Test
    public void testSubscriber07() {}

    @Test
    public void testSubscriber08() throws InterruptedException {
        final Mono<String> mono = Mono.just("hello ");

        Thread t =
                new Thread(
                        () ->
                                mono.map(msg -> msg + "thread ")
                                        .subscribe(
                                                v ->
                                                        System.out.println(
                                                                v
                                                                        + Thread.currentThread()
                                                                                .getName())));
        t.start();
        t.join();
    }

    @Test
    public void testSubscriber09() {
        Flux.just(1, 2, 0)
                .map(i -> "100 / " + i + " = " + (100 / i)) // this triggers an error with 0
                .onErrorReturn("Divided by zero :("); // error handling example
    }
}
