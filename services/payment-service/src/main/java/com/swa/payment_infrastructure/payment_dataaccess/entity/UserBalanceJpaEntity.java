package com.swa.payment_infrastructure.payment_dataaccess.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "user_balance")
@EntityListeners(AuditingEntityListener.class)
public class UserBalanceJpaEntity {
  @Id
  private UUID customerId;

  private BigDecimal balance;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime lastModifiedDate;
}
