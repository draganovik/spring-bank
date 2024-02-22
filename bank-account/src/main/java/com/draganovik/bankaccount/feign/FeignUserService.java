package com.draganovik.bankaccount.feign;

import com.draganovik.bankaccount.models.UserFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface FeignUserService {
    @GetMapping("/user-service/users/{email}")
    ResponseEntity<UserFeignResponse> getUserByEmail(@PathVariable String email, @RequestHeader("X-User-Role") String xUserRole);
}
