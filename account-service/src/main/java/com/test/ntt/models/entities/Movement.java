package com.test.ntt.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "movements")
@EntityListeners(AuditingEntityListener.class)
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_type")
    private Catalog movementType;

    @Column(name = "debit_amount", precision = 19, scale = 3, nullable = false)
    private BigDecimal debAmount;

    @Column(name = "current_balance", precision = 19, scale = 3, nullable = false)
    private BigDecimal currentBalance;

    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    public Movement(){}

    public Movement(Catalog movementType, BigDecimal debAmount, BigDecimal currentBalance,  Account account){
        this.movementType = movementType;
        this.debAmount = debAmount;
        this.currentBalance = currentBalance;
        this.account = account;
        this.status = true;


    }
}
