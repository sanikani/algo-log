package org.sani.algolog.global.error;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.sani.algolog.global.error.exception.NotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/test")
public class TestExceptionController {

    @GetMapping("/custom")
    public String custom() {
        throw new NotFoundException("Target resource does not exist.");
    }

    @PostMapping("/valid")
    public String valid(@Valid @RequestBody TestRequest request) {
        return "ok";
    }

    @GetMapping("/type")
    public String type(@RequestParam int count) {
        return "count=" + count;
    }

    @GetMapping("/required")
    public String required(@RequestParam String keyword) {
        return keyword;
    }

    @GetMapping("/unexpected")
    public String unexpected() {
        throw new IllegalStateException("boom");
    }

    public record TestRequest(
            @NotBlank(message = "title is required")
            String title
    ) {
    }
}
