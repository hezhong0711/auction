package com.tw.auction.repository;

import com.tw.auction.base.TestBase;
import com.tw.enums.MarginStatus;
import com.tw.repository.AuctionApply;
import com.tw.repository.AuctionApplyRespository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuctionApplyRepositoryTest extends TestBase {
    @Autowired
    AuctionApplyRespository auctionApplyRespository;

    @Test
    public void should_save_auction_apply_success_given_status_is_not_pay() {
        AuctionApply auctionApply = AuctionApply.builder()
                .accidentItemId(1L)
                .marginPrice(BigDecimal.valueOf(2000))
                .marginStatus(MarginStatus.NOT_PAY)
                .build();

        AuctionApply savedAuctionApply = auctionApplyRespository.save(auctionApply);
        assertNotNull(savedAuctionApply);
        assertEquals(MarginStatus.NOT_PAY, savedAuctionApply.getMarginStatus());
    }

    @Test
    public void should_update_auction_apply_success_given_status_is_not_pay() {
        AuctionApply auctionApply = AuctionApply.builder()
                .accidentItemId(1L)
                .marginPrice(BigDecimal.valueOf(2000))
                .build();

        AuctionApply savedAuctionApply = auctionApplyRespository.save(auctionApply);
        savedAuctionApply.setMarginStatus(MarginStatus.PAY);
        AuctionApply updatedAuctionApply = auctionApplyRespository.save(auctionApply);
        assertNotNull(updatedAuctionApply);
        assertEquals(MarginStatus.PAY, savedAuctionApply.getMarginStatus());
    }
}
