CREATE TABLE itens_cardapio  (

    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    descricao   VARCHAR(255),
    categoria   VARCHAR(50)  NOT NULL,
    preco       NUMERIC(10,2) NOT NULL,
    quantidade  INTEGER      NOT NULL DEFAULT 0,
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    foto_url    VARCHAR(500),
    restaurante_id BIGINT   NOT NULL,

    CONSTRAINT fk_cardapio_restaurante
        FOREIGN KEY (restaurante_id)
        REFERENCES restaurantes(id)

);

