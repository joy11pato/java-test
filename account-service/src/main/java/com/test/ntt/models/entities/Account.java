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
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "account_type")
    private Catalog accountType;

    @Column(name = "initial_balance", precision = 19, scale = 3, nullable = false)
    private BigDecimal initialBalance;

    private Boolean status;

    private Long idClient;

    public Account(){}

    public Account(String number, Catalog accountType, BigDecimal initialBalance, Long idClient){
        this.status = true;
        this.number = number;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
        this.idClient = idClient;
    }

}
