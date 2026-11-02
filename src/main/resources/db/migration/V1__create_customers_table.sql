CREATE TABLE customers (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           phone VARCHAR(255),

                           PRIMARY KEY (id),
                           CONSTRAINT uk_customers_email UNIQUE (email)
);