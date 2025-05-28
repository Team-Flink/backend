package spring.flink.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.flink.apiPayload.ApiResponse;
import spring.flink.apiPayload.exception.GeneralException;
import spring.flink.apiPayload.status.ErrorStatus;

@RestController
@RequestMapping("/api")
public class tempController {

    @GetMapping("/test1")
    public ResponseEntity<Object> test1() {
        throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
    }

    @PostMapping(value = "/test2")
    public ResponseEntity<Object> test2(@RequestBody @Valid RequestDTO request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(request));
    }

    @GetMapping("/test3")
    public ResponseEntity<Object> test3() {
        System.out.println(3/0);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @GetMapping("/test4")
    public ResponseEntity<Object> test4() {
        return ResponseEntity.ok(ApiResponse.created(null));
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDTO {
        @NotBlank
        @Length(min = 2, max = 5)
        private String name;
        @NotNull
        @Positive
        private int age;
    }
}
