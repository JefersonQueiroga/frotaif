# Roteiro incremental — FrotaIF

Cada incremento é uma **funcionalidade completa** (repository → service → controller),
fechada com um commit e uma tag `git`. Conceitos transversais (DTO, testes/CI, Security)
entram como refatoração sobre o que já existe.

| Tag | Incremento | Requisitos | Conceitos em destaque |
|---|---|---|---|
| `f0-base` | Entidades, enums, repositórios, dados de exemplo | Modelo de domínio | JPA, relacionamentos, `data.sql` |
| `f1-veiculos` | CRUD de veículos | RF01, RF02, RN01, RN02 | Camada de serviço, injeção por construtor, exceções de negócio, exclusão lógica |
| `f2-motoristas` | CRUD de motoristas + CNH vencendo | RF03, RF04, RN02, RN03 | Query methods com datas, reaproveitamento do padrão |
| `f3-solicitar-viagem` | Solicitar e listar viagens | RF05, RF09, RN04 | `@ConfigurationProperties`, validação de datas |
| `f4-aprovar-viagem` | Aprovar / rejeitar | RF06, RN05–RN08 | JPQL de conflito de período, `@Transactional`, regras compostas |
| `f5-execucao-viagem` | Iniciar / concluir / cancelar | RF07, RF08, RN08–RN10, RN14 | Máquina de estados, reaproveitamento de regra |
| `f6-abastecimentos` | Registrar e listar abastecimentos | RF10, RN11 | Recurso aninhado (`/veiculos/{id}/...`) |
| `f7-manutencoes` | Abrir / encerrar manutenção | RF11, RF12, RN12, RN13 | Efeito colateral transacional (cancelamento em lote) |
| `f8-relatorio` | Relatório de consumo | RF13 | Consultas agregadas (`SUM`), projeções |
| `t1-dto` | DTOs + MapStruct + `@RestControllerAdvice` | — | Aula 08 |
| `t2-testes-ci` | Testes unitários das regras + GitHub Actions | — | Aula 09 |
| `t3-security` | Login, papéis, BCrypt, auditoria `criadoPor` | Papéis | Aula 10 |

> Até `t1-dto`, os controllers recebem e devolvem as próprias entidades — de propósito,
> para motivar a aula de DTO.
