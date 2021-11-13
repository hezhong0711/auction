package com.tw.repository;

import com.tw.repository.entities.PayMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayMessageRepository extends JpaRepository<PayMessage, Long> {
}
