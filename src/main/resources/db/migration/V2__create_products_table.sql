CREATE TABLE products (

                          id BIGINT NOT NULL AUTO_INCREMENT,

                          name VARCHAR(150) NOT NULL,

                          description VARCHAR(1000),

                          sku VARCHAR(50) NOT NULL,

                          price DECIMAL(12,2) NOT NULL,

                          quantity INT NOT NULL DEFAULT 0,

                          active BOOLEAN NOT NULL DEFAULT TRUE,

                          PRIMARY KEY (id),

                          CONSTRAINT uk_products_sku UNIQUE (sku),

                          CONSTRAINT chk_products_price CHECK (price >= 0),

                          CONSTRAINT chk_products_quantity CHECK (quantity >= 0)
);