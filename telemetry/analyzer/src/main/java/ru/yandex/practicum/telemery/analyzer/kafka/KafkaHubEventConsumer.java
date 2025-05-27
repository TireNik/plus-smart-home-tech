package ru.yandex.practicum.telemery.analyzer.kafka;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Component
public class KafkaHubEventConsumer {

    private final KafkaConsumer<String, HubEventAvro> consumer;

    public KafkaHubEventConsumer(
            @Value("${kafka.hub-event.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.hub-event.group-id}") String groupId,
            @Value("${kafka.hub-event.key-deserializer}") String keyDeserializer,
            @Value("${kafka.hub-event.value-deserializer}") String valueDeserializer,
            @Value("${kafka.hub-event.auto-commit}") boolean autoCommit
    ) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(autoCommit));
        this.consumer = new KafkaConsumer<>(props);
    }

    public void subscribe(List<String> topics) {
        consumer.subscribe(topics);
    }

    public ConsumerRecords<String, HubEventAvro> poll(Duration timeout) {
        return consumer.poll(timeout);
    }

    public void commit() {
        consumer.commitSync();
    }

    public void close() {
        consumer.close();
    }

    @PreDestroy
    public void shutdown() {
        consumer.close();
    }

    public void wakeup() {
        consumer.wakeup();
    }
}
