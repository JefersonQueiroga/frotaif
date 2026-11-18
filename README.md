# FrotaIF — Gestão de Frota de Veículos do Campus

Projeto de demonstração da disciplina **Desenvolvimento de Sistemas Corporativos (DSC)** — TADS/IFRN Campus Pau dos Ferros.

- Requisitos: [`docs/REQUISITOS.md`](docs/REQUISITOS.md)
- Roteiro incremental: [`docs/ROTEIRO.md`](docs/ROTEIRO.md)

## Executar

```bash
mvn spring-boot:run   (ou "Run" na classe FrotaifApplication pela IDE)
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html (botão **Authorize** para fazer login)
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:frotaif`, usuário `sa`, sem senha)

## Login (Spring Security)

A API usa **HTTP Basic**: e-mail e senha em toda requisição. Todos os usuários do `data.sql` têm a senha `123456`
(no banco fica só o hash BCrypt).

| E-mail | Papel |
|---|---|
| `gestor@ifrn.edu.br` | GESTOR |
| `servidor@ifrn.edu.br`, `carla@ifrn.edu.br` | SERVIDOR |
| `diego@terceirizada.com`, `elias@terceirizada.com`, `fabio@terceirizada.com` | MOTORISTA |
| `inativo@ifrn.edu.br` | SERVIDOR inativo (login responde 401) |

```bash
curl -i -u gestor@ifrn.edu.br:123456 localhost:8080/api/veiculos
```

Sem credencial ou com senha errada: **401**. Autenticado em rota de outro papel: **403**.
As regras por rota ficam em `config/SecurityConfig.java`.

Com PostgreSQL:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```
