package com.ag04.batchui.dbqueue.service;

import com.ag04.batchui.dbqueue.consumer.QueueConsumerModule;
import com.ag04.batchui.dbqueue.domain.QueueingState;
import com.ag04.batchui.dbqueue.domain.JobExecutionParamDto;
import com.ag04.batchui.dbqueue.domain.StartJobCommand;
import com.ag04.batchui.dbqueue.repository.StartJobCommandRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class StartJobCommandConsumer implements QueueConsumerModule<Long> {
    private static final Logger log = LoggerFactory.getLogger(StartJobCommandConsumer.class);

    private static final String SELECT_QUEUED_ITEMS = "select sjc.id from com.ag04.batchui.dbqueue.domain.StartJobCommand sjc where sjc.sendingState.nextAttemptTime < :currentTime order by sjc.sendingState.nextAttemptTime asc";

    @Autowired
    private ApplicationContext ctx;

    private final EntityManager entityManager;

    private final StartJobCommandRepository repository;

    private final JobManagementService jobManagementService;

    private final ObjectMapper mapper;

    public StartJobCommandConsumer(
            EntityManager entityManager,
            StartJobCommandRepository repository,
            JobManagementService jobManagementService,
            ObjectMapper mapper
    ) {
        this.entityManager = entityManager;
        this.repository = repository;
        this.jobManagementService = jobManagementService;
        this.mapper = mapper;
    }

    @Override
    public List findItemIdsWhereQueueingNextAttemptTimeIsBefore(LocalDateTime localDateTime, int limit) {
        TypedQuery<Long> query = this.entityManager.createQuery(SELECT_QUEUED_ITEMS, Long.class);
        query.setParameter("currentTime", localDateTime);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Optional<QueueingState> getQueueingStateForItem(Long id) {
        return repository.findById(id).map(StartJobCommand::getSendingState);
    }

    @Override
    public Optional<QueueingState> processItem(Long itemId) {
        Optional<StartJobCommand> sjcOpt = repository.findById(itemId);
        if (sjcOpt.isPresent()) {
            StartJobCommand sjc = sjcOpt.get();
            try {
                List<JobExecutionParamDto> paramsList = mapper.readValue(sjc.getJobParams(), new TypeReference<List<JobExecutionParamDto>>() { });
                jobManagementService.startNewJobAsync(sjc.getJobName(), JobParamsUtil.convert(paramsList));

                return Optional.of(sjc.getSendingState());
            } catch(Exception ex) {
                log.error("Failed to process startJobCommand: ", ex);
                throw new CommandProcessingException(ex.getMessage());
            }
        } else {
            return Optional.empty();
        }
    }
}
