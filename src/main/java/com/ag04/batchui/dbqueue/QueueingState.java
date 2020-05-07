package com.ag04.batchui.dbqueue;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@Embeddable
public class QueueingState {
    private static final int LAST_ATTEMPT_ERROR_MESSAGE_LENGTH_LIMIT = 500;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QueueStatus status;

    @Column(name = "next_attempt_time")
    private LocalDateTime nextAttemptTime;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "last_attempt_time")
    private LocalDateTime lastAttemptTime;

    @Column(name = "last_attempt_error_message", length = 500)
    private String lastAttemptErrorMessage;

    public QueueingState() {
        this.status = QueueStatus.NOT_ATTEMPTED;
        this.attemptCount = 0;
    }

    public void scheduleNextAttempt(LocalDateTime nextAttemptTime) {
        this.nextAttemptTime = Objects.requireNonNull(nextAttemptTime);
    }

    void registerAttemptSuccess(LocalDateTime time) {
        this.attemptCount++;
        this.nextAttemptTime = null;
        this.lastAttemptTime = Objects.requireNonNull(time);

        this.status = QueueStatus.SUCCESS;
        this.lastAttemptErrorMessage = null; // clear error if exists
    }

    void registerAttemptFailure(LocalDateTime time, Throwable error) {
        this.attemptCount++;
        this.nextAttemptTime = null;
        this.lastAttemptTime = Objects.requireNonNull(time);

        this.status = QueueStatus.ERROR;
        this.lastAttemptErrorMessage = constructErrorMessage(error);
    }

    private String constructErrorMessage(Throwable error) {
        StringBuilder sb = new StringBuilder();

        Throwable th = error;
        do {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            String throwableMessage = th.getMessage();
            String msg = (throwableMessage == null || throwableMessage.trim().equals("")) ? th.getClass().getSimpleName() : throwableMessage;
            sb.append(msg);
        } while ((th = th.getCause()) != null);

        String text = sb.toString();

        // trim to limit if exceeds it
        return text.length() > LAST_ATTEMPT_ERROR_MESSAGE_LENGTH_LIMIT ? text.substring(0, LAST_ATTEMPT_ERROR_MESSAGE_LENGTH_LIMIT) : text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueueingState)) {
            return false;
        }
        QueueingState queueingState = (QueueingState) o;
        return attemptCount == queueingState.attemptCount &&
                status == queueingState.status &&
                Objects.equals(nextAttemptTime, queueingState.nextAttemptTime) &&
                Objects.equals(lastAttemptTime, queueingState.lastAttemptTime) &&
                Objects.equals(lastAttemptErrorMessage, queueingState.lastAttemptErrorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, nextAttemptTime, attemptCount, lastAttemptTime, lastAttemptErrorMessage);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QueueingState.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("nextAttemptTime=" + nextAttemptTime)
                .add("attemptCount=" + attemptCount)
                .add("lastAttemptTime=" + lastAttemptTime)
                .add("lastAttemptErrorMessage='" + lastAttemptErrorMessage + "'")
                .toString();
    }

    //--- set / get methods ---------------------------------------------------

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public LocalDateTime getNextAttemptTime() {
        return nextAttemptTime;
    }

    public void setNextAttemptTime(LocalDateTime nextAttemptTime) {
        this.nextAttemptTime = nextAttemptTime;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getLastAttemptTime() {
        return lastAttemptTime;
    }

    public void setLastAttemptTime(LocalDateTime lastAttemptTime) {
        this.lastAttemptTime = lastAttemptTime;
    }

    public String getLastAttemptErrorMessage() {
        return lastAttemptErrorMessage;
    }

    public void setLastAttemptErrorMessage(String lastAttemptErrorMessage) {
        this.lastAttemptErrorMessage = lastAttemptErrorMessage;
    }
}
