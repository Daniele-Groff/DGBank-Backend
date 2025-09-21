package com.dgbank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Test", description = "Endpoint di test")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "DG Bank API is running!"),
    })
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "Test API", description = "API di test")
    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "DG Bank API is running!");
        return response;
    }
}
