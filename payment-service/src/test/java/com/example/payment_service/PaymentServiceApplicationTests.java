package com.example.payment_service;

import com.example.payment_service.dto.BookingDto;
import com.example.payment_service.dto.PaymentRequestDto;
import com.example.payment_service.exception.PaymentException;
import com.example.payment_service.feign.BookingClient;
import com.example.payment_service.repository.PaymentRepository;
import com.example.payment_service.service.PaymentService;
import com.example.payment_service.entity.Payment;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceApplicationTests {

    @Mock
    private BookingClient bookingClient;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_rejectsAmountMismatch() {
        BookingDto booking = mock(BookingDto.class);
        when(booking.getTotalAmount()).thenReturn(200.0);
        when(bookingClient.getBookingById(7L)).thenReturn(booking);

        PaymentRequestDto request = new PaymentRequestDto();
        request.setBookingId(7L);
        request.setAmount(100.0);
        request.setCurrency("USD");
        request.setPaymentMethod("CREDIT_CARD");

        assertThrows(PaymentException.class,
                () -> paymentService.processPayment(request, "user1"));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processPayment_doesNotReportSuccessWhenBookingUpdateFails() {
        BookingDto booking = mock(BookingDto.class);
        when(booking.getTotalAmount()).thenReturn(100.0);
        when(bookingClient.getBookingById(7L)).thenReturn(booking);
        when(paymentRepository.findByBookingId(7L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingClient.updateBookingStatusInternal(
                eq(7L), eq("PAID"), any(String.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build());

        PaymentRequestDto request = new PaymentRequestDto();
        request.setBookingId(7L);
        request.setAmount(100.0);
        request.setCurrency("USD");
        request.setPaymentMethod("CREDIT_CARD");

        assertThrows(PaymentException.class,
                () -> paymentService.processPayment(request, "user1"));

        verify(paymentRepository, times(2)).save(any(Payment.class));
    }
}
