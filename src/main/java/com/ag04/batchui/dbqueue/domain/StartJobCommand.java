package com.ag04.batchui.dbqueue.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "start_job_command", indexes = @Index(name = "idx_start_job_queue_polling_fields", columnList = "next_attempt_time"))
public class StartJobCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "job_name", length = 80, nullable = false)
    private String jobName;

    @Column(name = "job_params")
    private String jobParams;

    private QueueingState sendingState = new QueueingState();

    public StartJobCommand status(QueueStatus status) {
        sendingState.setStatus(status);
        return this;
    }

    public StartJobCommand nextAttemptTime(LocalDateTime nextAttemptTime) {
        sendingState.setNextAttemptTime(nextAttemptTime);
        return this;
    }

    public StartJobCommand attemptCount(int attemptCount) {
        sendingState.setAttemptCount(attemptCount);
        return this;
    }

    public StartJobCommand lastAttemptTime(LocalDateTime lastAttemptTime) {
        sendingState.setLastAttemptTime(lastAttemptTime);
        return this;
    }

    public StartJobCommand lastAttemptErrorMessage(String lastAttemptErrorMessage) {
        sendingState.setLastAttemptErrorMessage(lastAttemptErrorMessage);
        return this;
    }

    //--- set / get methods ---------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public StartJobCommand jobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobParams() {
        return jobParams;
    }

    public void setJobParams(String jobParams) {
        this.jobParams = jobParams;
    }

    public StartJobCommand jobParams(String jobParams) {
        this.jobParams = jobParams;
        return this;
    }

    //-- sendingState methods -------------------------------------------------

    public QueueStatus getStatus() {
        return sendingState.getStatus();
    }

    public void setStatus(QueueStatus status) {
        sendingState.setStatus(status);
    }

    public LocalDateTime getNextAttemptTime() {
        return sendingState.getNextAttemptTime();
    }

    public void setNextAttemptTime(LocalDateTime nextAttemptTime) {
        sendingState.setNextAttemptTime(nextAttemptTime);
    }

    public Integer getAttemptCount() {
        return sendingState.getAttemptCount();
    }

    public void setAttemptCount(Integer attemptCount) {
        sendingState.setAttemptCount(attemptCount);
    }

    public LocalDateTime getLastAttemptTime() {
        return sendingState.getLastAttemptTime();
    }

    public void setLastAttemptTime(LocalDateTime lastAttemptTime) {
        sendingState.setLastAttemptTime(lastAttemptTime);
    }

    public String getLastAttemptErrorMessage() {
        return sendingState.getLastAttemptErrorMessage();
    }

    public void setLastAttemptErrorMessage(String lastAttemptErrorMessage) {
        sendingState.setLastAttemptErrorMessage(lastAttemptErrorMessage);
    }

    //--- public methods ------------------------------------------------------

    public QueueingState getSendingState() {
        return sendingState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StartJobCommand startJobCommand = (StartJobCommand) o;
        if (startJobCommand.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), startJobCommand.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "InvoiceNotification{" +
                "id=" + getId() +
                ", jobName='" + jobName + "'" +
                ", sendingState='" + sendingState + "'" +
                "}";
    }
}
