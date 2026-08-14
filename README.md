# 👤 Microsserviço de Usuário

Microsserviço responsável pelo **gerenciamento de usuários e autenticação** da aplicação Agendador de Tarefas.

Desenvolvido utilizando **Java 17 e Spring Boot**, aplicando conceitos de APIs REST, persistência de dados, validações, tratamento de exceções e segurança com JWT.

## 🚀 Tecnologias

* Java 17
* Spring Boot
* Spring Data
* Spring Security
* JWT
* MongoDB
* Docker
* Gradle
* JUnit
* Mockito
* GitHub Actions

## 📌 Responsabilidades

O microsserviço é responsável por:

* Cadastro de usuários
* Consulta de usuários
* Autenticação
* Geração de token JWT
* Validação de credenciais
* Controle de acesso
* Persistência dos dados dos usuários
* Validação dos dados recebidos pela API

## 🔐 Autenticação

A autenticação utiliza **Spring Security e JWT**.

Após o usuário realizar o login com suas credenciais, o serviço realiza a autenticação e disponibiliza um token JWT para ser utilizado nas requisições aos endpoints protegidos.

```text
Cliente
   │
   │ Login
   ▼
Usuario Service
   │
   │ JWT
   ▼
Cliente
   │
   │ Bearer Token
   ▼
API protegida
```

## 🗄️ Banco de dados

O serviço utiliza **PostgreSQL** para persistência dos usuários.

## 🧪 Testes

Os testes são desenvolvidos utilizando:

* JUnit
* Mockito
---

## ▶️ Como executar

### Pré-requisitos

* Java 17
* Docker
* Docker Compose
* Git

Clone o projeto:

```bash
git clone https://github.com/LiaraFreitas/usuario.git
```

Execute a aplicação utilizando sua IDE ou Gradle.

## 🔗 Projeto completo

Este microsserviço faz parte do projeto **Agendador de Tarefas**, composto por:

* [Agendador de Tarefas](https://github.com/LiaraFreitas/agendador-tarefas)
* [Notificação](https://github.com/LiaraFreitas/notificacao)
* [BFF](https://github.com/LiaraFreitas/bff-agendador-tarefas)
