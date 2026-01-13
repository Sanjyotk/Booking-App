package com.SpringBoot.Sanjyot.AirbnbClone.controllers;

import com.SpringBoot.Sanjyot.AirbnbClone.services.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("webhook")
public class WebhookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endPointString;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader){
        try{
            Event event = Webhook.constructEvent(payload,sigHeader,endPointString);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        }catch (SignatureVerificationException e){
            throw new RuntimeException(e);
        }
    }
}

