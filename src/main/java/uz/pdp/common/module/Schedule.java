package uz.pdp.common.module;

import lombok.*;

import java.util.concurrent.ScheduledExecutorService;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Schedule {
    private Long number;
    private ScheduledExecutorService schedule;
}
