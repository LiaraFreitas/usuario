
# 🗂️ Agendador de Tarefas - Microsserviços em Java

Uma breve descrição sobre o que esse projeto faz e para quem ele é

Este projeto está sendo desenvolvido durante o curso da Javanauta, com foco em **Java, Spring Boot e Arquitetura de Microsserviços**.

A proposta é criar um **Agendador de Tarefas** composto por diferentes microsserviços, aplicando conceitos de **POO, APIs REST, Banco de Dados Relacional, Segurança com JWT, CI/CD e metodologias ágeis**.

## 📌 Status do Projeto

No momento, o projeto está em desenvolvimento.
O primeiro microsserviço entregue foi o de **Cadastro de Usuário**.

**Microsserviços planejados**:

- ✅ Cadastro de Usuário (finalizado)
- ⏳ Agendador de Tarefas (em andamento)
- ⏳ Notificação por Email (em andamento)
- ⏳ BFF (Back For Frontend) (em andamento)


## ⚙️ Tecnologias Utilizadas

- ☕ **Java 17**
- 🌱 **Spring Boot** (Web, Data JPA, Security, Validation)
- 🗄️ **Banco de Dados Relacional** (PostgreSQL/MySQL)
- 🔐 **Spring Security + JWT** (autenticação e autorização)
- 📬 **Postman** (testes de requisições HTTP)
- 🔄 **GitHub Actions** (CI/CD)
- 🛠️ **Gradle** (build e gerenciamento de dependências)
- 🗂️ **Metodologias Ágeis** (Kanban, Git Flow com branches main, develop e feature)


## 🚀 Funcionalidades do Microsserviço de Usuário

 - Cadastro de usuários
 - Atualização de dados
 - Listagem de usuários
 - Exclusão de registros
 - Autenticação e autorização com Spring Security + JWT
 - Testes de rotas com Postman


## 🧪 Testes de API (Usuário)

As rotas disponíveis foram testadas com **Postman**:

 - `POST /usuarios` → Criar novo usuário
 - `GET /usuarios` → Listar usuários
 - `PUT /usuarios/{id}` → Atualizar usuário
 - `DELETE /usuarios/{id}` → Deletar usuário
 - `POST /login` → Autenticação e geração de token JWT

 ## 📂 Estrutura do Projeto (parcial)
 
 ```shell
 /agendador-tarefas
├── usuario # Microsserviço de usuários (entregue)
│ ├── src/main/java  # Código principal
│ ├── src/test/java  # Testes unitários
├── agendador        # Microsserviço de tarefas (em desenvolvimento)
├── notificacao      # Microsserviço de emails (planejado)
├── bff              # Microsserviço BFF (planejado)
└── README.md
```

## ▶️ Como Rodar o Microsserviço de Usuário

**1.** Clone este repositório:
```bash
git clone https://github.com/SEU-USUARIO/usuario.git
```

**2.** Clone este repositório:
```bash
cd usuario
```
**3.** Configure o banco de dados no arquivo **application.properties**.

**4.** Utilize o **Postman** para testar as rotas.

## 👨‍💻 Autor

Desenvolvido por **Liara Freitas** no curso da Javanauta.
