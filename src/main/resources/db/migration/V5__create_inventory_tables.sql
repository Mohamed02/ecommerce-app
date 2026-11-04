CREATE TABLE inventory (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           product_id BIGINT NOT NULL,
                           total_stock INT NOT NULL DEFAULT 0,
                           reserved_stock INT NOT NULL DEFAULT 0,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           PRIMARY KEY (id),

                           CONSTRAINT uk_inventory_product
                               UNIQUE (product_id),

                           CONSTRAINT fk_inventory_product
                               FOREIGN KEY (product_id)
                                   REFERENCES products(id),

                           CONSTRAINT chk_inventory_total_stock
                               CHECK (total_stock >= 0),

                           CONSTRAINT chk_inventory_reserved_stock
                               CHECK (reserved_stock >= 0),

                           CONSTRAINT chk_inventory_reserved_limit
                               CHECK (reserved_stock <= total_stock)
);

CREATE TABLE inventory_reservations (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        order_id BIGINT NOT NULL,
                                        product_id BIGINT NOT NULL,
                                        quantity INT NOT NULL,
                                        status VARCHAR(30) NOT NULL,
                                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                        PRIMARY KEY (id),

                                        CONSTRAINT fk_reservation_order
                                            FOREIGN KEY (order_id)
                                                REFERENCES orders(id),

                                        CONSTRAINT fk_reservation_product
                                            FOREIGN KEY (product_id)
                                                REFERENCES products(id),

                                        CONSTRAINT chk_reservation_quantity
                                            CHECK (quantity > 0)
);
INSERT INTO inventory (product_id, total_stock, reserved_stock)
SELECT id, quantity, 0
FROM products;