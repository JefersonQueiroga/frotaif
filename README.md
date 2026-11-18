# FrotaIF — Gestão de Frota de Veículos do Campus

Projeto de demonstração da disciplina **Desenvolvimento de Sistemas Corporativos (DSC)** — TADS/IFRN Campus Pau dos Ferros.

- Requisitos: [`docs/REQUISITOS.md`](docs/REQUISITOS.md)
- Roteiro incremental: [`docs/ROTEIRO.md`](docs/ROTEIRO.md)

## Executar

```bash
mvn spring-boot:run   (ou "Run" na classe FrotaifApplication pela IDE)
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html (login em **Autenticação**, depois botão **Authorize**)
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:frotaif`, usuário `sa`, sem senha)

## Login (Spring Security + JWT)

A API usa **token JWT**. O cliente manda e-mail e senha **uma vez** em `POST /api/auth/login` e recebe um
`accessToken`, válido por 60 minutos (`frota.jwt.expiracao-minutos`). Nas demais requisições, envia o cabeçalho
`Authorization: Bearer <accessToken>`.

Todos os usuários do `data.sql` têm a senha `123456` (no banco fica só o hash BCrypt).

| E-mail | Papel |
|---|---|
| `gestor@ifrn.edu.br` | GESTOR |
| `servidor@ifrn.edu.br`, `carla@ifrn.edu.br` | SERVIDOR |
| `diego@terceirizada.com`, `elias@terceirizada.com`, `fabio@terceirizada.com` | MOTORISTA |
| `inativo@ifrn.edu.br` | SERVIDOR inativo (login responde 401) |

```bash
# 1. Login: devolve {"accessToken": "...", "tokenType": "Bearer", "expiresIn": 3600}
curl -s -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
     -d '{"email": "gestor@ifrn.edu.br", "senha": "123456"}'

# 2. Demais rotas: o token no cabeçalho
curl -i localhost:8080/api/veiculos -H "Authorization: Bearer <accessToken>"
```

Login com senha errada, sem token, token alterado ou expirado: **401**. Token válido em rota de outro papel: **403**.
As regras por rota ficam em `config/SecurityConfig.java`; a emissão e a conferência do token, em `config/JwtConfig.java`.

O segredo que assina os tokens vem de `frota.jwt.segredo`. Em produção, defina a variável de ambiente
`FROTA_JWT_SEGREDO` (no mínimo 32 caracteres): quem conhece o segredo consegue forjar tokens.

Com PostgreSQL:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```
