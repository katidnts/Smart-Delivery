# 🍽️ SmartDelivery - Módulo de Restaurantes

O **SmartDelivery** é uma aplicação backend voltada para o gerenciamento de restaurantes em um sistema de delivery.

Este módulo é responsável por centralizar as operações relacionadas aos restaurantes, permitindo seu cadastro, manutenção e consulta.

---

## 🚀 Funcionalidades

* Cadastro de restaurantes
* Atualização de dados (total e parcial)
* Inativação de restaurantes
* Consulta por ID
* Listagem paginada de restaurantes
* Associação de restaurantes com endereço
* Preenchimento automático de endereço a partir do CEP

---

## 🌐 Integrações

A aplicação realiza integração com uma **API externa** para consulta de CEP, permitindo o preenchimento automático de dados de endereço.

Isso reduz erros de entrada de dados e melhora a experiência do usuário durante o cadastro.

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas, organizada para separar responsabilidades e facilitar a manutenção:

* **Controller** → Interface de entrada da API
* **Service** → Regras de negócio
* **Repository** → Persistência de dados
* **Domain** → Representação das entidades do sistema

---

## 🗄️ Persistência

O controle de versionamento do banco de dados é realizado por meio de migrations, garantindo evolução estruturada e consistente do schema ao longo do tempo.

---

## 📬 Endpoints

A API expõe endpoints REST para gerenciamento de restaurantes, incluindo operações de criação, atualização, listagem e inativação.

Link para a coleção do Postman com exemplos de requisições para todos os endpoints.

👉 https://go.postman.co/collection/45750874-1d03bffe-a01b-4f25-9c77-9df56e4780ea?source=collection_link

---

## 🔧 Tecnologias Utilizadas

* Java
* Spring Boot
* Spring Data JPA
* Flyway
* Maven

---

## ▶️ Como executar o projeto

```bash
git clone https://github.com/katidnts/Smart-Delivery.git

cd Smart-Delivery

./mvnw spring-boot:run
```

---

## 📬 Roadmap do Projeto

As próximas melhorias e evoluções estão organizadas no Trello:

👉 https://trello.com/b/QUc5XSBv/smart-delivery
