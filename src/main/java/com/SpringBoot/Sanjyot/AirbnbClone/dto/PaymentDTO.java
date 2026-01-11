package com.SpringBoot.Sanjyot.AirbnbClone.dto;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.BookingEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long transactionId;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private Long bookingId;
}
