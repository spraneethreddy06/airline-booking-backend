package com.example.payment_service.feign;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;

import com.example.payment_service.dto.BookingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service", configuration = com.example.payment_service.config.FeignConfig.class)
public interface BookingClient {

    @GetMapping("/api/bookings/{id}")
    BookingDto getBookingById(@PathVariable("id") Long id);
    
    @PutMapping("/api/admin/bookings/{id}/status")
    void updateBookingStatus(@PathVariable("id") Long id, @RequestParam("status") String status);

    @PutMapping("/api/admin/bookings/internal/{id}/status")
    ResponseEntity<Void> updateBookingStatusInternal(@PathVariable("id") Long id, @RequestParam("status") String status, @RequestHeader("X-Service-Secret") String serviceSecret);

}

