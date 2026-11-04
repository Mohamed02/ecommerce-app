CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_id BIGINT NOT NULL,
                          amount DECIMAL(12, 2) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          payment_method VARCHAR(30) NOT NULL,
                          transaction_reference VARCHAR(100),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_order_id
    ON payments(order_id);