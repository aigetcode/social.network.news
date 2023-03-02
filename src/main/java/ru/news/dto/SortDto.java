package ru.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дто для сортировки", defaultValue = "sorting=\"field, direction\"")
public class SortDto {
    private String fieldName;
    private Sort.Direction sortOrder;

    public static Sort toSort(String[] sortDto) {
        if (sortDto == null || sortDto.length < 1)
            return Sort.by(Sort.Direction.ASC, "id");

        List<Sort.Order> orders = new ArrayList<>();
        if (sortDto[0].contains(",")) {
            for (String sortOrder : sortDto) {
                String[] sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sortDto[1]), sortDto[0]));
        }
        return Sort.by(orders);
    }

    private static Sort.Direction getSortDirection(String direction) {
        return direction.contains("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }
}