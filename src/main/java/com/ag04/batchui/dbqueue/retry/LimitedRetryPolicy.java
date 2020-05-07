package com.ag04.batchui.dbqueue.retry;

import java.time.LocalDateTime;
import java.util.Optional;

public class LimitedRetryPolicy implements RetryPolicy {
    private final int attemptCountLimit;
    private final RetryPolicy delegate;

    public LimitedRetryPolicy(int attemptCountLimit, RetryPolicy delegate) {
        if (attemptCountLimit < 1) {
            throw new IllegalArgumentException("Attempt count limit cannot be less than 1, but is " + attemptCountLimit);
        }
        this.attemptCountLimit = attemptCountLimit;
        this.delegate = delegate;
    }

    @Override
    public Optional<LocalDateTime> calculateNextAttemptTime(LocalDateTime lastAttemptTime, int attemptCount) {
        if (attemptCount < this.attemptCountLimit) {
            return this.delegate.calculateNextAttemptTime(lastAttemptTime, attemptCount);
        } else {
            return Optional.empty();
        }
    }
}
