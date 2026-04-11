CREATE TABLE enderecos  (

   id BIGSERIAL PRIMARY KEY,
   logradouro VARCHAR(100) NOT NULL,
   bairro VARCHAR(100) NOT NULL,
   cep VARCHAR(9) NOT NULL,
   complemento VARCHAR(100),
   numero VARCHAR(20),
   uf VARCHAR(2) NOT NULL,
   cidade VARCHAR(100) NOT NULL

);

CREATE TABLE restaurantes (

    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    id_endereco BIGINT NOT NULL,

    CONSTRAINT fk_id_endereco
        FOREIGN KEY (id_endereco) REFERENCES enderecos(id)

);