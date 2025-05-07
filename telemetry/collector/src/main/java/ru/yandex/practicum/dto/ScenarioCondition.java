package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.model.OperationType;

@Getter
@Setter
@ToString
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ScenarioCondition {
    @NotBlank
    String sensorId;
    @NotNull
    ConditionType type;
    @NotNull
    OperationType operation;
    @NotNull
    Integer value;
}