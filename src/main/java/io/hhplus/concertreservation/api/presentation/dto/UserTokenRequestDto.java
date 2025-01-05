package io.hhplus.concertreservation.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenRequestDto {

    private int user_id;
    private int concert_id;
    private String status;
    private LocalDateTime entered_at;
    private LocalDateTime expired_at;

}
