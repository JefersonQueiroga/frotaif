# FrotaIF — Gestão de Frota de Veículos do Campus

**Documento de Requisitos — v1.0** · 30/09/2026
Disciplina: Desenvolvimento de Sistemas Corporativos (DSC) — TADS / IFRN Campus Pau dos Ferros
Prof. Jeferson Queiroga Pereira

> Projeto de demonstração usado pelo professor para apresentar o conteúdo da disciplina em aula,
> construído de forma incremental (uma camada por aula). Não é entregue aos alunos como trabalho.

---

## 1. Visão geral

API REST para gerenciar a frota de veículos do campus: cadastro de veículos e motoristas
(terceirizados), solicitação e aprovação de viagens, controle de abastecimentos e manutenções
e relatório de consumo.

**Stack:** Java 21 · Spring Boot 4.1 · Spring Web · Spring Data JPA · Bean Validation · PostgreSQL
(H2 nas primeiras aulas) · MapStruct · Spring Security · JUnit 5/Mockito · GitHub Actions.

---

## 2. Atores / papéis

| Papel | Responsabilidades |
|---|---|
| **SERVIDOR** | Solicita viagens; consulta e cancela as próprias solicitações |
| **MOTORISTA** | Inicia e conclui as viagens atribuídas a ele |
| **GESTOR** | Aprova/rejeita viagens alocando veículo e motorista; CRUD de veículos e motoristas; registra abastecimentos e manutenções; acessa relatórios |

---

## 3. Modelo de domínio

### Usuario
| Campo | Tipo | Observação |
|---|---|---|
| id | Long | |
| nome | String | obrigatório |
| email | String | único, usado no login |
| senha | String | hash (BCrypt) — a partir da aula de Security |
| papel | Enum | SERVIDOR, MOTORISTA, GESTOR |
| ativo | boolean | exclusão lógica |

### Veiculo
| Campo | Tipo | Observação |
|---|---|---|
| id | Long | |
| placa | String | única, formato Mercosul `AAA0A00` |
| marca, modelo | String | obrigatórios |
| ano | Integer | |
| tipo | Enum | CARRO, VAN, ONIBUS |
| capacidadePassageiros | Integer | > 0 |
| quilometragemAtual | Long | ≥ 0 |
| status | Enum | DISPONIVEL, EM_VIAGEM, EM_MANUTENCAO, INATIVO |

### Motorista (terceirizado)
| Campo | Tipo | Observação |
|---|---|---|
| id | Long | |
| nome | String | obrigatório |
| cpf | String | único, dígito verificador validado |
| registroCnh | String | único |
| categoriaCnh | Enum | B, C, D, E |
| validadeCnh | LocalDate | |
| usuario | Usuario | `@OneToOne` — login com papel MOTORISTA |
| ativo | boolean | exclusão lógica |

### Viagem
| Campo | Tipo | Observação |
|---|---|---|
| id | Long | |
| solicitante | Usuario | `@ManyToOne` |
| destino, finalidade | String | obrigatórios |
| saida | LocalDateTime | |
| retornoPrevisto | LocalDateTime | > saida |
| qtdPassageiros | Integer | > 0 |
| veiculo | Veiculo | definido na aprovação |
| motorista | Motorista | definido na aprovação |
| kmInicial, kmFinal | Long | preenchidos ao iniciar/concluir |
| status | Enum | ver seção 5 |
| motivo | String | motivo de rejeição/cancelamento |

### Abastecimento
| Campo | Tipo | Observação |
|---|---|---|
| id | Long | |
| veiculo | Veiculo | `@ManyToOne` (vinculado só ao veículo) |
| data | LocalDate | |
| litros | BigDecimal | > 0 |
| valorTotal | BigDecimal | > 0 |
| kmNoAbastecimento | Long | |
| posto | String | |

### Manutencao
| Campo | Tipo | Observação |
|---|---|---|
| id | Long | |
| veiculo | Veiculo | `@ManyToOne` |
| tipo | Enum | PREVENTIVA, CORRETIVA |
| descricao | String | |
| dataInicio | LocalDate | |
| dataPrevistaFim | LocalDate | obrigatória |
| dataFim | LocalDate | preenchida no encerramento |
| custo | BigDecimal | informado no encerramento |

### Auditoria (todas as entidades)
`criadoEm`, `atualizadoEm`, `criadoPor` — via Spring Data JPA Auditing (`criadoPor` a partir da aula de Security).

---

## 4. Requisitos funcionais

### Veículos
- **RF01** — Cadastrar, editar, consultar por id e listar veículos, com filtros por `status` e `tipo`.
- **RF02** — Inativar veículo (`DELETE` realiza exclusão lógica) e reativá-lo.

### Motoristas
- **RF03** — Cadastrar, editar, consultar, listar e inativar motoristas (exclusão lógica).
- **RF04** — Listar motoristas com CNH vencendo nos próximos N dias.

### Viagens
- **RF05** — Servidor solicita viagem (destino, finalidade, saída, retorno previsto, nº de passageiros).
- **RF06** — Gestor aprova a solicitação alocando veículo e motorista, ou rejeita informando o motivo.
- **RF07** — Motorista inicia a viagem e depois a conclui informando o km final.
- **RF08** — Solicitante ou gestor cancela a viagem.
- **RF09** — Listar viagens com filtros por status, período e solicitante.

### Abastecimentos
- **RF10** — Registrar e listar os abastecimentos de um veículo.

### Manutenções
- **RF11** — Abrir manutenção (preventiva/corretiva) com data prevista de término.
- **RF12** — Encerrar manutenção informando data de fim e custo.

### Relatório
- **RF13** — Relatório de consumo por veículo e período: km rodados, litros, km/l, gasto com
  combustível e gasto com manutenção.

---

## 5. Ciclo de vida da viagem

```
SOLICITADA ──aprovar──▶ APROVADA ──iniciar──▶ EM_ANDAMENTO ──concluir──▶ CONCLUIDA
    │                      │
    ├──rejeitar──▶ REJEITADA
    └──cancelar──▶ CANCELADA ◀──cancelar / manutenção──┘
```

---

## 6. Regras de negócio

| ID | Regra | Erro |
|---|---|---|
| **RN01** | Placa única e no formato Mercosul (`AAA0A00`). | 400 / 409 |
| **RN02** | Exclusão lógica: veículos, motoristas e usuários nunca são apagados; `DELETE` marca como INATIVO. Listagens retornam só ativos por padrão. | — |
| **RN03** | CPF e registro da CNH do motorista são únicos; CPF com dígito verificador válido (custom validator). | 400 / 409 |
| **RN04** | Solicitação com antecedência mínima configurável em `frota.viagem.antecedencia-minima-horas` (padrão 48). | 422 |
| **RN05** | `qtdPassageiros` ≤ `capacidadePassageiros` do veículo alocado. | 422 |
| **RN06** | Veículo só pode ser alocado se estiver DISPONIVEL e sem outra viagem APROVADA/EM_ANDAMENTO no mesmo período. | 409 |
| **RN07** | Motorista ativo e sem outra viagem APROVADA/EM_ANDAMENTO no mesmo período. | 409 |
| **RN08** | CNH válida na data da viagem; VAN e ONIBUS exigem categoria D ou superior. Verificada na aprovação **e novamente ao iniciar**. | 422 |
| **RN09** | Transições de status só conforme a seção 5 (ex.: não se conclui viagem não iniciada; só o motorista alocado inicia/conclui). | 422 / 403 |
| **RN10** | Ao iniciar: `kmInicial` = km atual do veículo; veículo → EM_VIAGEM. Ao concluir: `kmFinal` > `kmInicial`; atualiza `quilometragemAtual`; veículo → DISPONIVEL. | 422 |
| **RN11** | Abastecimento: km informado ≥ último km conhecido do veículo; se maior que a `quilometragemAtual`, atualiza-a. | 422 |
| **RN12** | Abrir manutenção: bloqueada se o veículo estiver EM_VIAGEM (409). Caso contrário, veículo → EM_MANUTENCAO e as viagens APROVADAS do veículo dentro do período `[dataInicio, dataPrevistaFim]` são canceladas automaticamente com motivo registrado. | 409 |
| **RN13** | Encerrar manutenção: veículo → DISPONIVEL; custo obrigatório (≥ 0). | 422 |
| **RN14** | Solicitante só cancela a própria viagem e apenas enquanto SOLICITADA ou APROVADA. | 403 / 422 |

---

## 7. Endpoints

| Método | Rota | Papel |
|---|---|---|
| GET/POST | `/api/veiculos` (`?status=&tipo=`) | GESTOR (GET: todos) |
| GET/PUT/DELETE | `/api/veiculos/{id}` | GESTOR |
| PATCH | `/api/veiculos/{id}/reativar` | GESTOR |
| GET/POST | `/api/motoristas` | GESTOR |
| GET/PUT/DELETE | `/api/motoristas/{id}` | GESTOR |
| GET | `/api/motoristas/cnh-vencendo?dias=30` | GESTOR |
| POST | `/api/viagens` | SERVIDOR |
| GET | `/api/viagens` (`?status=&inicio=&fim=&solicitanteId=`) | todos (SERVIDOR vê só as suas) |
| GET | `/api/viagens/{id}` | todos |
| PATCH | `/api/viagens/{id}/aprovar` (body: veiculoId, motoristaId) | GESTOR |
| PATCH | `/api/viagens/{id}/rejeitar` (body: motivo) | GESTOR |
| PATCH | `/api/viagens/{id}/iniciar` | MOTORISTA |
| PATCH | `/api/viagens/{id}/concluir` (body: kmFinal) | MOTORISTA |
| PATCH | `/api/viagens/{id}/cancelar` (body: motivo) | SERVIDOR / GESTOR |
| GET/POST | `/api/veiculos/{id}/abastecimentos` | GESTOR |
| GET/POST | `/api/veiculos/{id}/manutencoes` | GESTOR |
| PATCH | `/api/manutencoes/{id}/encerrar` (body: dataFim, custo) | GESTOR |
| GET | `/api/relatorios/consumo?veiculoId=&inicio=&fim=` | GESTOR |

Erros padronizados via `@RestControllerAdvice` (ProblemDetail / RFC 9457).

---

## 8. Configuração

```yaml
frota:
  viagem:
    antecedencia-minima-horas: 48
```
Lida via `@ConfigurationProperties`.

---

## 9. Fora do escopo

Dados da empresa terceirizada · notificações (e-mail/WhatsApp) · controle de atraso no retorno ·
frontend · upload de documentos. Paginação (`Pageable`) é opcional, como detalhe da aula de JPA.

---

## 10. Roteiro incremental por aula

| Etapa | Aula (slides) | O que entra no FrotaIF |
|---|---|---|
| 1 | 04 — Projeto e Controller | Spring Initializr; `VeiculoController` com CRUD em `List` em memória; `@PathVariable`, `@RequestParam`, `ResponseEntity` |
| 2 | 05 — JPA | Entidades e relacionamentos (`@ManyToOne`, `@OneToOne`), enums, repositórios, query methods, JPQL de conflito de período, auditoria |
| 3 | 06 — Injeção de Dependência | Injeção por construtor; `@ConfigurationProperties` da antecedência |
| 4 | 07 — Camada de Serviço | `ViagemService` com RN04–RN10 e RN14; `ManutencaoService` com RN12–RN13; `@Transactional`; exceções de negócio |
| 5 | 08 — DTO e MapStruct | Requests/Responses (records), mappers MapStruct, Bean Validation (incl. validador de CPF), `@RestControllerAdvice` |
| 6 | 09 — CI/CD | Testes unitários das regras (conflito, CNH, transições) + GitHub Actions |
| 7 | 10 — Spring Security | Usuários, papéis, BCrypt, autorização por endpoint, `criadoPor` da auditoria |

---

## 11. Histórico de decisões

- Motoristas são terceirizados: identificados por CPF e registro da CNH; sem dados da empresa.
- Motorista tem login próprio (papel MOTORISTA) e é quem inicia/conclui a viagem.
- Gestor aprova e aloca veículo e motorista.
- Abrir manutenção cancela automaticamente viagens aprovadas no período; bloqueada se o veículo estiver em viagem.
- Antecedência mínima configurável no `application.yml`.
- Abastecimento vinculado só ao veículo e atualiza a quilometragem quando maior.
- Exclusão lógica para todos os cadastros.
- CNH revalidada ao iniciar a viagem.
- Auditoria com Spring Data JPA Auditing.
