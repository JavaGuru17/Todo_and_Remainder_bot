package uz.pdp.common.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@ToString

public class ToDo {
    private String description;
    private Long userId;
    private LocalDate createdDate;
    private Long number;
}
