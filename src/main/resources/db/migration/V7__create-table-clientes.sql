CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sobrenome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    id_endereco BIGINT NOT NULL,

    CONSTRAINT fk_id_endereco
        FOREIGN KEY (id_endereco) REFERENCES enderecos(id)

);