# 🍽️ SmartDelivery - Módulo de Restaurantes

Este módulo da aplicação **SmartDelivery** é responsável pelo gerenciamento de restaurantes, incluindo operações CRUD, estrutura de endereço e versionamento de banco de dados com Flyway.

---

## 🚀 Funcionalidades

* Cadastro de restaurantes
* Integração com API externa para busca de endereço por CEP
* Preenchimento automático de dados como rua, bairro, cidade e estado
* Atualização de dados (total e parcial)
* Inativação de restaurantes
* Associação de restaurante com endereço
* Listagem de restaurantes com paginação
* Busca de restaurantes pelo ID


---

## 🌐 Integrações externas

A aplicação realiza integração com a API pública do ViaCEP para consulta de endereços a partir do CEP informado.

### 🔄 Fluxo de funcionamento

1. O usuário informa o CEP no cadastro do restaurante
2. O backend realiza uma requisição HTTP para a API ViaCEP
3. Os dados retornados (logradouro, bairro, cidade e UF) são utilizados para completar automaticamente o endereço
4. O endereço completo é associado ao restaurante antes de ser salvo no banco

### 🎯 Benefícios

- Redução de erros de digitação
- Melhor experiência do usuário
- Padronização dos dados de endereço
- Menor dependência de entrada manual de dados

---

## 🛠️ Detalhes técnicos

A integração com a API de CEP foi implementada utilizando o RestClient do Spring Framework.

Foi criado um serviço dedicado (`CepService`) responsável por:

- Realizar a chamada HTTP para a API externa
- Validar o CEP informado
- Tratar respostas inválidas (ex: CEP inexistente)
- Mapear os dados retornados para um DTO interno (`EnderecoParcialDTO`)

A aplicação utiliza DTOs distintos para cada responsabilidade:

- Entrada de dados (`DadosEnderecoRequestDTO`) → recebe apenas CEP, número e complemento
- Resposta da API (`DadosEnderecoResponseDTO`) → retorna o endereço completo para o cliente da API
- Integração externa (`CepResponseDTO`) → representa a resposta da API ViaCEP
- DTO intermediário (`EnderecoParcialDTO`) → utilizado para desacoplar a API externa da lógica interna
- Domínio interno → entidade `Endereco`

Essa separação garante baixo acoplamento com a API externa e maior facilidade de manutenção.

---

## 🧱 Estrutura do Projeto

### 📦 Pacotes principais

```
br.com.katidantas.smartdelivery
│
├── endereco
│   ├── CepController
│   ├── CepService
│   ├── CepResponseDTO
│   ├── Endereco
│   ├── EnderecoParcialDTO
│   ├── DadosEnderecoRequestDTO
│   ├── DadosEnderecoResponseDTO
│   └── RestClientConfig
├── restaurante
│   ├── Restaurante
│   ├── RestauranteController
│   ├── RestauranteService
│   ├── RestauranteRepository
│   ├── DadosRestauranteDTO
│   ├── DadosAtualizacaoRestauranteDTO
│   ├── DadosDetalhamentoRestauranteDTO
│   └── DadosListaRestauranteDTO
```

---

## 🏗️ Arquitetura

A aplicação segue uma separação em camadas:

* **Controller** → Recebe requisições HTTP
* **Service** → Contém regras de negócio
* **Repository** → Comunicação com o banco de dados
* **DTOs** → Transporte de dados entre camadas
* **Entities** → Representação das tabelas no banco

---

## 🧾 Entidades

### Restaurante

Representa o restaurante no sistema.

Principais características:

* Nome
* Status (ativo/inativo)
* Endereço associado

---

### Endereco

Representa a localização do restaurante.

Exemplos de dados:

* Rua
* Cidade
* Estado
* CEP

---

## 🔄 DTOs

Os DTOs são usados para evitar exposição direta das entidades.

* `DadosRestauranteDTO` → Entrada de dados para criação
* `DadosAtualizacaoRestauranteDTO` → Atualização de dados
* `DadosDetalhamentoRestauranteDTO` → Retorno detalhado
* `DadosListaRestauranteDTO` → Representação simplificada para listagem
* `DadosEnderecoRequestDTO` → Entrada de dados do endereço
* `DadosEnderecoResponseDTO` → Retorno de dados do endereço
* `EnderecoParcialDTO` → DTO intermediário para integração com CEP

---

## 🗄️ Banco de Dados

O projeto utiliza **Flyway** para versionamento do banco.

### 📂 Migrations

* `V1__create-table-enderecos-restaurantes.sql`
  Criação das tabelas de endereço e restaurante

* `V2__alter-table-restaurantes-add-column-ativo.sql`
  Adição da coluna de status (ativo/inativo)

---

## 📬 Endpoints disponíveis

### ➕ Criar restaurante

`POST /restaurantes`

#### Exemplo de request:

```json
{
  "nome": "Dogão gourmet",
  "telefone": "2199984400",
  "endereco": {
    "cep": "22220001",
    "numero": "214",
    "complemento": "2"
  }
}
```

---

### ✏️ Atualizar restaurante

`PUT /restaurantes/{id}`

---

### 🔧 Atualização parcial

`PATCH /restaurantes/{id}`

---

### ❌ Inativar restaurante

`DELETE /restaurantes/{id}`

---


### 📄 Listar restaurantes (paginado)

`GET /restaurantes`

#### Parâmetros de query:

- `page` → número da página (default: 0)
- `size` → quantidade de registros por página (default: 10)
- `sort` → ordenação (ex: nome,asc)

#### Exemplo de requisição:

GET /restaurantes?page=0&size=10&sort=nome,asc

---
## 📮 Collection do Postman

A collection para testes da API está disponível no projeto:

📁 `/postman/SmartDelivery.postman_collection.json`

### Como usar:

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo dentro da pasta `postman`
4. Execute os endpoints disponíveis

---

## 🔧 Tecnologias Utilizadas

* Java
* Spring Boot
* Spring Data JPA
* Flyway
* Maven

---

## ▶️ Como executar o projeto

# Clonar repositório
git clone https://github.com/katidnts/Smart-Delivery.git

cd Smart-Delivery

./mvnw spring-boot:run
---

## 📌 Observações

* Uso de DTOs para desacoplamento entre camadas
* Separação clara de responsabilidades
* Versionamento do banco com Flyway

---

## 📬 Roadmap do Projeto

As próximas melhorias e evoluções do sistema estão organizadas no Trello:

👉 https://trello.com/b/QUc5XSBv/smart-delivery

Algumas melhorias planejadas:


- Implementação de testes unitários
- Implementação de testes de integração
- Criação de consultas parametrizadas (queries com parâmetros) para otimização de buscas