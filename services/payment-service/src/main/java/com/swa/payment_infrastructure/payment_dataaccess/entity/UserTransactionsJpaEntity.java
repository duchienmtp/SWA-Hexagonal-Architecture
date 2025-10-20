package com.swa.payment_infrastructure.payment_dataaccess.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.swa.payment_domain.valueobject.PaymentMethod;
import com.swa.payment_domain.valueobject.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_transactions")
@EntityListeners(AuditingEntityListener.class)
public class UserTransactionsJpaEntity {
  @Id
  private UUID id;

  private UUID orderId;

  private UUID customerId;

  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;
  
  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime lastModifiedDate;
}
