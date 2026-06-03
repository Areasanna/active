
---

```markdown
# Active — Sistema de Gestão de Treinos

O **Active** é uma plataforma full-stack completa para o gerenciamento, planejamento e acompanhamento de treinos físicos e periodizações de musculação.

O ecossistema é composto por duas partes que funcionam de forma integrada:
* **Backend (Active API):** Uma API REST robusta desenvolvida em Java com Spring Boot 3.x, persistência em banco de dados e segurança via tokens JWT.
* **Frontend (Active Web):** Uma aplicação web responsiva, fluida e minimalista desenvolvida em HTML, CSS e JavaScript Vanilla (puro), focada em performance e sem dependências externas.

---

## 🏗️ Arquitetura do Projeto

O projeto adota um modelo cliente-servidor totalmente desacoplado:

* **Arquitetura de Segurança:** Autenticação Stateless via tokens JWT (JSON Web Tokens). O frontend armazena o token no `localStorage` e o envia no cabeçalho `Authorization: Bearer <token>` de cada requisição. O backend intercepta as requisições (`OncePerRequestFilter`), valida o token e injeta o contexto no Spring Security.
* **Perfis de Acesso (RBAC):** Controle rígido de rotas e componentes baseado na regra do usuário:

| Role | Permissões |
| :--- | :--- |
| **USER** | Visualiza exercícios, cria/visualiza planos e sessões, e consulta recordes pessoais (PR). |
| **ADMIN** | Possui privilégios totais (`USER` + criar, editar e deletar exercícios, músculos, equipamentos e usuários). |

---

## ☕ Backend: Active API (Spring Boot)

A API gerencia todas as regras de negócio, persistência de dados e segurança do ecossistema.

### Stack Tecnológica
* **Ambiente de Execução:** Java 17 / 21
* **Framework Base:** Spring Boot 3.x
* **Persistência de Dados:** Spring Data JPA + Banco de Dados H2
* **Segurança:** Spring Security + JJJWT (Java JWT)
* **Documentação:** Springdoc OpenAPI UI (Swagger)

### Como Executar o Backend

1. **Clonar o repositório:**
   ```bash
   git clone [https://github.com/Areasanna/active.git](https://github.com/Areasanna/active.git)
   cd active-api

```

2. **Configurar as Variáveis de Ambiente:** Crie um arquivo `.env` na raiz da pasta do backend:
```env
DB_USERNAME=seu_usuario_aqui
DB_PASSWORD=sua_senha_aqui
JWT_SECRET=coloque_uma_chave_segura_aqui

```


3. **Massa de Dados Inicial (Opcional):** Para popular o catálogo de exercícios e músculos automaticamente no primeiro boot, certifique-se de preencher o arquivo `src/main/resources/import.sql` (`spring.sql.init.mode=always`).
4. **Compilar e Testar (JaCoCo):**
```bash
./mvnw clean test

```


*Relatório de cobertura disponível em: `target/site/jacoco/index.html*`
5. **Rodar a Aplicação:**
```bash
./mvnw spring-boot:run

```



* **API rodando em:** http://localhost:8080
* **Swagger UI:** http://localhost:8080/swagger-ui/index.html
* **H2 Console:** http://localhost:8080/h2-console

---

## 🌐 Frontend: Active Web (Vanilla JS)

Interface web limpa, rápida e construída do zero, sem a necessidade de compiladores ou frameworks pesados.

### Stack Tecnológica

* HTML5 e CSS3 modernos (CSS Variables, Flexbox, Grid)
* JavaScript ES2020+ nativo
* Google Fonts: Barlow Condensed, Barlow, JetBrains Mono

### Estrutura de Arquivos

```text
frontend/
├── index.html              # Entrada da aplicação (carrega CSS e JS na ordem correta)
├── css/
│   └── styles.css          # Estilos globais: design tokens, layouts e componentes
└── js/
    ├── app.js              # Inicialização e ponto de entrada do JS
    ├── state.js            # Estado global da aplicação, controle de login/logout e decodificação do JWT
    ├── api.js              # Centralização de requests HTTP (Variável constante API = 'http://localhost:8080')
    ├── router.js           # Mecanismo de renderização de páginas e controle da Sidebar
    ├── errors.js           # Dicionário e tratamento de mensagens de erro contextuais
    ├── ui.js               # Componentes visuais globais (Toasts, Modais, Alertas inline e Spinners)
    ├── helpers.js          # Utilitários de formatação, escape de caracteres e loaders
    └── pages/              # Módulos de visualização de dados e telas do sistema
        ├── login.js        ├── exercises.js    ├── equipment.js    ├── sessions.js
        ├── dashboard.js    ├── muscles.js      ├── plans.js        └── users.js

```

### Como Executar o Frontend

Por ser JavaScript puro, você precisa apenas de um servidor estático local para evitar problemas de CORS e caminhos de arquivo:

* **VS Code:** Instale a extensão *Live Server*, clique com o botão direito no `index.html` e selecione *Open with Live Server*.
* **IntelliJ IDEA:** Abra o `index.html` e clique no ícone do navegador no canto superior direito do editor (porta padrão `63342`).
* **Via Terminal (Python):** `python3 -m http.server 8000` (Acesse http://localhost:8000)
* **Via Terminal (Node.js):** `npx serve .`

---

## 🛠️ Malha de Endpoints e Integração

O frontend consome os seguintes recursos expostos pela API do backend:

| Método | Rota | Descrição | Acesso |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Realiza a autenticação e retorna o Token JWT. | Público |
| `POST` | `/users` | Cadastro de novos usuários na plataforma. | Público |
| `GET` | `/users` | Listagem paginada de usuários (Filtros: name, email, active). | **ADMIN** |
| `DELETE` | `/users/{id}` | Remoção ou inativação lógica de usuário. | **ADMIN** |
| `GET` | `/exercises` | Listar catálogo de exercícios com filtros avançados. | USER / ADMIN |
| `POST` | `/exercises` | Criar novo exercício no catálogo global. | **ADMIN** |
| `PUT` | `/exercises/{id}` | Atualizar dados cadastrais de um exercício. | **ADMIN** |
| `DELETE` | `/exercises/{id}` | Deletar um exercício do sistema. | **ADMIN** |
| `GET` | `/exercises/{id}/personal-record` | Retorna o Recorde Pessoal (PR) do usuário conectado. | USER / ADMIN |
| `GET` | `/muscles` | Listar grupos musculares cadastrados. | USER / ADMIN |
| `POST` | `/muscles` | Cadastrar novo grupo muscular. | **ADMIN** |
| `GET` | `/equipments` | Listagem paginada de equipamentos de treino. | USER / ADMIN |
| `POST` | `/equipments` | Cadastrar novo equipamento. | **ADMIN** |
| `GET` | `/training-plans` | Listar fichas e planos de treino do usuário logado. | USER / ADMIN |
| `POST` | `/training-plans` | Criar plano de treino (vínculo automático ao usuário). | USER / ADMIN |
| `GET` | `/workout-sessions` | Consultar histórico de treinos executados no período. | USER / ADMIN |
| `POST` | `/workout-sessions` | Registrar a execução de uma sessão de treino diária. | USER / ADMIN |

---

## 🛡️ Resiliência e Tratamento de Erros

O ecossistema foi projetado para lidar com falhas de maneira elegante:

* **Tratamento de Erros no Frontend:** A classe `ApiError` mapeia o erro em propriedades claras (`message`, `status`, `type`). As respostas com status `401` limpam o cache local instantaneamente e redirecionam o usuário de volta à tela de autenticação.
* **Feedback ao Usuário:** Formulários exibem mensagens de validação com alertas inline (`alert$`), enquanto ações globais assíncronas (como deleções ou erros de rede) disparam notificações flutuantes (`toast`) no canto inferior da tela.

```

```

