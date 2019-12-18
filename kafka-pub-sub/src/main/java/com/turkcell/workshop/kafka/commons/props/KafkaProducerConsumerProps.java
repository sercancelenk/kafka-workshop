package com.turkcell.workshop.kafka.commons.props;

import com.turkcell.workshop.kafka.commons.dto.Consumer;
import com.turkcell.workshop.kafka.commons.dto.Producer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
@ConfigurationProperties(prefix="kafka")
public class KafkaProducerConsumerProps {
    private Consumer consumerWithAutoCommit;
    private Producer producer;
    private Consumer consumerWithAutoCommitBatch;
    private Consumer consumerWithManualAckSingle;
}
