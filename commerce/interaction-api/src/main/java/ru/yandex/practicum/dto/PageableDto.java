package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PageableDto {

    @Min(value = 0)
    Integer page = 0;

    @Min(value = 1)
    Integer size = 10;

    List<String> sort = new ArrayList<>(List.of("productName"));
}