package com.ag04.batchui.dbqueue.consumer;

import com.ag04.batchui.dbqueue.domain.QueueingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueConsumerModule<ID> {

    List<ID> findItemIdsWhereQueueingNextAttemptTimeIsBefore(LocalDateTime time, int limit);

    Optional<QueueingState> getQueueingStateForItem(ID itemId);

    Optional<QueueingState> processItem(ID itemId);
}
