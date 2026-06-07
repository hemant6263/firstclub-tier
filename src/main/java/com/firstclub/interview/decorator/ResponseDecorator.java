package com.firstclub.interview.decorator;

import com.firstclub.interview.dto.ApiMeta;
import com.firstclub.interview.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.UUID;

@Component
public class ResponseDecorator {

    private static final String API_VERSION = "v1";

    public <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        ApiMeta meta = new ApiMeta(requestId(), Instant.now(), API_VERSION);
        return ResponseEntity.ok(new ApiResponse<>(data, meta));
    }

    private String requestId() {
        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            String upstream = request.getHeader("X-Request-Id");
            return upstream != null ? upstream : UUID.randomUUID().toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }
}
