package com.ag04.batchui.dbqueue.retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ExponentialDelayRetryPolicy implements RetryPolicy {
    private final Duration initialDelay;
    private final double delayIncreaseFactor; // if the value is 1, then we don't have increase, but constant delay

    public ExponentialDelayRetryPolicy(Duration initialDelay, double delayIncreaseFactor) {
        this.initialDelay = Objects.requireNonNull(initialDelay);
        this.delayIncreaseFactor = delayIncreaseFactor;
        if (delayIncreaseFactor < 1) {
            throw new IllegalArgumentException("Delay increase factor cannot be less than 1, but is " + delayIncreaseFactor);
        }
    }

    @Override
    public Optional<LocalDateTime> calculateNextAttemptTime(LocalDateTime lastAttemptTime, int attemptCount) {
        long initialDelayMillis = initialDelay.toMillis();
        long delayMillis = (long) (initialDelayMillis * Math.pow(delayIncreaseFactor, (double) (attemptCount - 1)));
        Duration delay = Duration.ofMillis(delayMillis);
        return Optional.of(lastAttemptTime.plus(delay));
    }
}
