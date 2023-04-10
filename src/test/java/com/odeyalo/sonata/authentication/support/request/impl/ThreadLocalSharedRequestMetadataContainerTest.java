package com.odeyalo.sonata.authentication.support.request.impl;

import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadata;
import com.odeyalo.sonata.authentication.testing.mock.MockSharedRequestMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ThreadLocalSharedRequestMetadataContainer}
 */
class ThreadLocalSharedRequestMetadataContainerTest {
    private final Logger logger = LoggerFactory.getLogger(ThreadLocalSharedRequestMetadataContainerTest.class);

    @Test
    @DisplayName("Set shared request metadata in single thread and expect metadata to be set")
    void setSharedRequestMetadataInSingleThread_andExpectMetadataToBeSet() {
        // given
        ThreadLocalSharedRequestMetadataContainer container = new ThreadLocalSharedRequestMetadataContainer();
        MockSharedRequestMetadata metadata = new MockSharedRequestMetadata();

        // when
        container.setSharedRequestMetadata(metadata);

        // then
        SharedRequestMetadata actual = container.getCurrentRequest();

        assertThat(actual)
                .as("THe request must be saved for current thread!")
                .isEqualTo(metadata);
    }

    @Test
    @DisplayName("Set shared request metadata in multiple threads and expect metadata to be set")
    void setSharedRequestMetadataInMultipleThreads_andExpectMetadataToBeSetForSpecificThread() {
        int threadsNumber = 5;
        ExecutorService pool = Executors.newFixedThreadPool(threadsNumber);
        // given
        ThreadLocalSharedRequestMetadataContainer container = new ThreadLocalSharedRequestMetadataContainer();

        // then
        for (int i = 0; i < threadsNumber; i++) {
            pool.execute(() -> {
                        // Create a random MockSharedRequestMetadata and expect that this request in equal to one that was saved before
                        MockSharedRequestMetadata metadata = MockSharedRequestMetadata.random();
                        container.setSharedRequestMetadata(metadata);
                        SharedRequestMetadata actual = container.getCurrentRequest();
                        logger.debug("Set the value: {}, actual: {}", metadata, actual);
                        assertThat(actual)
                                .as("THe request must be saved for this thread: %s!", Thread.currentThread().getName())
                                .isEqualTo(metadata);
                    }
            );
        }

        SharedRequestMetadata afterChildThreadsInvoked = container.getCurrentRequest();

        assertThat(afterChildThreadsInvoked)
                .as("SharedRequestMetadata must be null if it was not previously set in this thread: %s!", Thread.currentThread().getName())
                .isNull();

    }

    @Test
    @DisplayName("Get current request from single thread and expect metadata to return")
    void getCurrentRequestFromSingleThread_andExpectRequestToReturn() {
        // given
        ThreadLocalSharedRequestMetadataContainer container = new ThreadLocalSharedRequestMetadataContainer();
        MockSharedRequestMetadata metadata = new MockSharedRequestMetadata();

        // when
        container.setSharedRequestMetadata(metadata);

        // then
        SharedRequestMetadata actual = container.getCurrentRequest();

        assertThat(actual)
                .as("THe request must be saved for current thread!")
                .isEqualTo(metadata);
    }

    @Test
    @DisplayName("Get current request from multiple threads and expect metadata to return")
    void getCurrentRequestFromMultipleThreads_andExpectRequestToReturn() {
        // given
        ExecutorService pool = Executors.newFixedThreadPool(2);
        MockSharedRequestMetadata request = MockSharedRequestMetadata.random();
        ThreadLocalSharedRequestMetadataContainer container = new ThreadLocalSharedRequestMetadataContainer();

        // Save the Request in the thread and expect that it will be saved for child thread only
        pool.execute(() -> {
            container.setSharedRequestMetadata(request);
            // when
            SharedRequestMetadata actual = container.getCurrentRequest();
            // then
            assertEquals(request, actual, "The request must be saved for current thread!");
        });
        // expect that in other thread request is null
        pool.execute(() -> {
            SharedRequestMetadata actual = container.getCurrentRequest();
            assertNull(actual, "The request must be null, if request was not previously saved and must return Request only for current thread!");
        });
    }

    @Test
    @DisplayName("Get current request from single thread and expect null")
    void getRequestFromSingleThread_andExpectNull() {
        // given
        ThreadLocalSharedRequestMetadataContainer container = new ThreadLocalSharedRequestMetadataContainer();
        // when
        SharedRequestMetadata actual = container.getCurrentRequest();
        // then
        assertNull(actual, "SharedRequestMetadata must be null if it was not set!");
    }
}
