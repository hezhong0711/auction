CREATE TABLE IF NOT EXISTS auction_apply
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    accident_item_id BIGINT,
    margin_status    VARCHAR(255),
    margin_price     NUMERIC
);
