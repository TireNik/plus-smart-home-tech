package ru.yandex.practicum.aggregator.kafka;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class KafkaProperties {
    String bootstrapServers;
    Topics topics;
    Consumer consumer;
    Producer producer;

    @Getter @Setter
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class Topics {
        String sensorEvents;
        String snapshots;
    }

    @Getter @Setter
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class Consumer {
        String groupId;
        boolean enableAutoCommit;
        String keyDeserializer;
        String valueDeserializer;
    }

    @Getter @Setter
    public static class Producer {
        String keySerializer;
        String valueSerializer;
    }

}
