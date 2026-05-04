ALTER TABLE restaurantes
ALTER COLUMN cnpj SET NOT NULL,
ADD CONSTRAINT uk_restaurante_cnpj UNIQUE (cnpj);
