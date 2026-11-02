CREATE TABLE orders (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        customer_id BIGINT NOT NULL,
                        order_date TIMESTAMP NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        total_amount DECIMAL(12,2) NOT NULL,

                        PRIMARY KEY (id),

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id)
                                REFERENCES customers(id),

                        CONSTRAINT chk_orders_total_amount
                            CHECK (total_amount >= 0)
);

CREATE TABLE order_items (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,

                             product_name VARCHAR(150) NOT NULL,
                             sku VARCHAR(50) NOT NULL,

                             quantity INT NOT NULL,
                             unit_price DECIMAL(12,2) NOT NULL,
                             line_total DECIMAL(12,2) NOT NULL,

                             PRIMARY KEY (id),

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id),

                             CONSTRAINT fk_order_items_product
                                 FOREIGN KEY (product_id)
                                     REFERENCES products(id),

                             CONSTRAINT chk_order_items_quantity
                                 CHECK (quantity > 0),

                             CONSTRAINT chk_order_items_unit_price
                                 CHECK (unit_price >= 0),

                             CONSTRAINT chk_order_items_line_total
                                 CHECK (line_total >= 0)
);