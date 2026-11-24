# VETCARE 3D — pacote de desenvolvimento 2.0

## Tecnologias
- Java 21
- Spring Boot 3.4.5
- Spring Data JPA / Hibernate
- MySQL pelo XAMPP
- HTML, CSS e JavaScript
- Sessão HTTP e senhas BCrypt

## Instalação no Windows
1. Instale JDK 21 e Maven. No terminal, confira `java -version` e `mvn -version`.
2. Abra o XAMPP e inicie MySQL.
3. No phpMyAdmin, importe `database/criar_banco.sql` ou crie o banco `vetcare3d` com `utf8mb4_unicode_ci`.
4. Abra esta pasta no VS Code.
5. Abra o terminal integrado na pasta que contém `pom.xml`.
6. Execute `mvn spring-boot:run`.
7. Espere o Spring Boot iniciar e acesse `http://localhost:8080/login.html`.

## Primeiro acesso
- Usuário: `admin`
- Senha inicial: `Admin@12345`

O primeiro administrador é criado automaticamente quando a tabela de usuários está vazia. **Altere a senha inicial antes de utilizar com dados reais.** O pacote inclui criação de usuários pela tela Usuários (somente administrador), com perfis ADMIN, VET, RECEPTION, STOCK e FINANCE. Nesta versão os perfis são armazenados e exibidos, mas ainda não há matriz completa de autorização por módulo.

## Módulos nesta versão
- Login/logout por sessão; senha armazenada com BCrypt; criação de usuários pelo administrador.
- Painel; tutores, animais, profissionais e agenda com operações básicas.
- Prontuários e prescrições.
- Produtos, lotes, movimentações de entrada/saída, alerta de estoque mínimo.
- Registro de vacinação.
- Solicitação e atualização de exames (laboratório simulado).
- Cobranças e registro de pagamento simulado.
- Notificações internas/simuladas.
- Auditoria de algumas operações.
- Testes unitários de estratégia de preço e transições de estado.

## Limitações que precisam ser concluídas antes de chamar a entrega acadêmica de completa
Este é um pacote ampliado para teste local, mas ainda não satisfaz integralmente todos os requisitos do enunciado:
- Não foi executado neste ambiente contra o MySQL do seu computador; compatibilidade depende de JDK/Maven/MySQL e configuração locais.
- Alteração forçada da senha inicial, recuperação de senha, CSRF, rate limit e política robusta de sessão precisam ser adicionados.
- Perfis estão cadastrados, mas a autorização fina por função ainda precisa ser implementada.
- Prontuários precisam de tela de histórico detalhado, edição controlada e anexos; prescrições precisam de fluxo completo e impressão.
- Estoque precisa de transações de banco com isolamento, rastreabilidade de consumo clínico, validação de validade e exclusões seguras.
- Vacinação precisa de protocolos configuráveis e lembretes automáticos.
- Exames não possuem integração real; o laboratório é representado por fluxo manual/simulado.
- Faturamento não tem orçamento detalhado, itens de conta, descontos, estorno ou gateway.
- Notificações são apenas simuladas, não enviam e-mail/SMS/WhatsApp reais.
- Auditoria ainda não registra snapshots de valores anteriores em todas as operações.
- O enunciado pede pelo menos seis padrões de projeto aplicados e justificados. Foram incluídos exemplos de Strategy (cálculo de preço), Factory (canal de notificação simulada), Observer (evento de consulta auditado), Adapter (normalização de resultado de laboratório), State (transições de status da consulta) e Builder (mensagem de notificação). A documentação acadêmica ainda precisa descrever participantes, justificativa e consequências de cada padrão.
- A suíte JUnit ainda é parcial; adicionar testes de integração e regras de negócio.
- Diagramas UML e documentação arquitetural completa ainda precisam ser produzidos.

Não utilize dados pessoais reais nem publique o servidor na internet até concluir controles de acesso, segurança, backup e testes. Faça backup do banco antes de atualizações.
