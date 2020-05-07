package com.ag04.batchui.dbqueue.retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class FixedDelayRetryPolicy implements RetryPolicy {
    private final Duration delay;

    public FixedDelayRetryPolicy(Duration delay){
        this.delay = Objects.requireNonNull(delay);
    }

    @Override
    public Optional<LocalDateTime> calculateNextAttemptTime(LocalDateTime lastAttemptTime, int attemptCount) {
        return Optional.of(lastAttemptTime.plus(delay));
    }
    
}
