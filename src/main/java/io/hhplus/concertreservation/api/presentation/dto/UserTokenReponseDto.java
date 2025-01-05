package io.hhplus.concertreservation.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenReponseDto {
    private String token;
    private int queuePosition;

    public String getToken(){
        return token;
    }

    public int getQueuePosition(){
        return queuePosition;
    }
}
