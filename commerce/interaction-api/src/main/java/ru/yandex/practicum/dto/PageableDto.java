package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PageableDto {
    @Min(value = 0)
    Integer page;
    @Min(value = 1)
    Integer size;
    List<String> sort = new ArrayList<>();
}
