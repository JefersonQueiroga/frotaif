# FrotaIF — Gestão de Frota de Veículos do Campus

Projeto de demonstração da disciplina **Desenvolvimento de Sistemas Corporativos (DSC)** — TADS/IFRN Campus Pau dos Ferros.

- Requisitos: [`docs/REQUISITOS.md`](docs/REQUISITOS.md)
- Roteiro incremental: [`docs/ROTEIRO.md`](docs/ROTEIRO.md)

## Executar

```bash
mvn spring-boot:run   (ou "Run" na classe FrotaifApplication pela IDE)
```

- API: http://localhost:8080
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:frotaif`, usuário `sa`, sem senha)

Com PostgreSQL:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```
