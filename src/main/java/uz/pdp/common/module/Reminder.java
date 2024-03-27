package uz.pdp.common.module;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Reminder {
    private final UUID id = UUID.randomUUID();
    private String description;
    private Long userId;
    private LocalDateTime dateTime;
    private Long number;
    private String type;
}
