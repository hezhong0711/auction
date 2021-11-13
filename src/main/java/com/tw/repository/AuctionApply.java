package com.tw.repository;

import com.tw.enums.MarginStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionApply {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long accidentItemId;

    @Enumerated(EnumType.STRING)
    private MarginStatus marginStatus;

    private BigDecimal marginPrice;
}
