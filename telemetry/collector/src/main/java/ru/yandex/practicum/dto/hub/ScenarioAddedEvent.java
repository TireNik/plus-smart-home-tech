package ru.yandex.practicum.dto.hub;

import ru.yandex.practicum.dto.DeviceAction;
import ru.yandex.practicum.dto.ScenarioCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.HubEventType;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    String name;
    @NotEmpty
    List<ScenarioCondition> conditions;
    @NotEmpty
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}