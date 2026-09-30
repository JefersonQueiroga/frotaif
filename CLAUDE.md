# FrotaIF — instruções para o assistente

Projeto de demonstração da disciplina DSC (IFRN). O professor apresenta o código em aula,
então **clareza didática vale mais que sofisticação**.

## Fontes da verdade
- Requisitos: `docs/REQUISITOS.md` (IDs RFxx / RNxx)
- Ordem de implementação: `docs/ROTEIRO.md`

## Regras
- Implemente **somente o incremento pedido** (ex.: "implemente f1-veiculos"). Não antecipe
  conceitos de incrementos futuros: sem DTO/MapStruct antes de `t1-dto`, sem Spring Security
  antes de `t3-security`, sem testes antes de `t2-testes-ci` (salvo pedido explícito).
- Cada funcionalidade é completa: repository (consultas necessárias) → service (regras) → controller.
- Regras de negócio ficam **no service**, nunca no controller. Cite o ID da regra em comentário
  curto (`// RN06`).
- Injeção de dependência por construtor (campos `private final`).
- Exceções de negócio: `RecursoNaoEncontradoException` (404), `RegraNegocioException` (422),
  `ConflitoException` (409). Enquanto não houver `@RestControllerAdvice`, use
  `@ResponseStatus` nas próprias exceções.
- Nomes em português, seguindo o domínio (Veiculo, Viagem, Motorista...).
- Java 21, Spring Boot 4.1 (Spring Framework 7, Hibernate 7, Jackson 3), Lombok nas entidades (`@Getter @Setter @NoArgsConstructor`, nunca `@Data`).
- Ao final de cada incremento, sugerir mensagem de commit citando os requisitos.
