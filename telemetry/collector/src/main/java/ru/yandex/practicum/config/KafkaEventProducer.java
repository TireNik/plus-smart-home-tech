package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaEventProducer {

    final KafkaConfig config;

    public Future<RecordMetadata> send(String topic, String key, SpecificRecordBase value) {
        return config.getProducer().send(new ProducerRecord<>(topic, key, value));
    }

    public void close() {
        config.getProducer().close();
    }
}
