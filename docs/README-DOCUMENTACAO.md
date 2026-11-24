# VETCARE 3D — Documentação técnica

## Objetivo
Sistema web de apoio à gestão de uma clínica veterinária: tutores, animais, profissionais, agenda, prontuários, prescrições, estoque, vacinação, exames, faturamento, notificações simuladas e auditoria.

## Tecnologias
Java 21, Spring Boot 3.4.5, Spring Data JPA/Hibernate, MySQL, Spring MVC, HTML, CSS, JavaScript, Maven, JUnit 5 e Mockito.

## Arquitetura
O backend está organizado em `controller`, `model`, `repository`, `service`, `security` e `patterns`. A API REST usa `/api`. Parte dos módulos complementares acessa o MySQL por `JdbcTemplate`.

## Documentos
- [Arquitetura, requisitos e padrões](ARQUITETURA-E-PADROES.md)
- [Diagramas UML](DIAGRAMAS-UML.md)
- [Roteiro de apresentação](ROTEIRO-APRESENTACAO.md)

## Execução
Consulte `README-LEIA-ME.md` na raiz do repositório.

## Limites conhecidos
Esta documentação descreve o código revisado, mas não declara que todos os requisitos estejam completos. O README principal registra limitações: autorização fina por perfil, segurança adicional, protocolos configuráveis de vacinação, faturamento completo, integrações reais, auditoria com valores anteriores e cobertura de testes mais ampla.

Os oito testes relatados em 23/11/2026 foram executados na máquina de desenvolvimento, não reproduzidos no repositório remoto. Antes da entrega, execute `mvn clean test` e valide o fluxo com MySQL ativo.
