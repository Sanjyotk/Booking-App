package com.SpringBoot.Sanjyot.AirbnbClone.services;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.BookingEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.BookingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final BookingRepository bookingRepository;

    @Transactional
    public String getCheckoutSession(BookingEntity booking, String successUrl, String failureUrl) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(customerParams);

            //create session and pass session parms, pass booking information and user information
            //session params created for stripe, so stripe create a session booking for user

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomer(customer.getId())
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .putMetadata("bookingId", booking.getId().toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(booking.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName() + " : " + booking.getRoom().getType())
                                                                    .setDescription("Booking id: " + booking.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionParams);

            booking.setPaymentSessionId(session.getId());
            booking.setPaymentIntentId(session.getPaymentIntent());
            bookingRepository.save(booking);
            return session.getUrl();
        }
        catch (StripeException e){
            throw new RuntimeException(e);
        }
    }
}
