package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Size {
    @Column(name = "width", nullable = false)
    double width;
    @Column(name = "height", nullable = false)
    double height;
    @Column(name = "depth", nullable = false)
    double depth;
}