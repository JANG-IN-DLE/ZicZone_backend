package org.zerock.ziczone.dto.error;

import lombok.*;

@Builder
@ToString
@NoArgsConstructor
@Data
public class ErrorResponse {
    private String message;
    private int code;

    public ErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
