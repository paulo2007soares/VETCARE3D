# Arquitetura, requisitos e Design Patterns

## Visão geral
Aplicação web de gestão veterinária. Backend Java 21, Spring Boot 3.4.5, Spring MVC, Spring Data JPA/Hibernate e MySQL. Interface em `src/main/resources/static`; API REST sob `/api`.

## Requisitos funcionais contemplados
- CRUD de tutores, animais e profissionais.
- Painel com totais.
- Cadastro/edição de consultas e verificação de conflito de horário por profissional.
- Validação de transições de status.
- Prontuários e prescrições.
- Produtos, lotes, movimentações e alertas de estoque mínimo.
- Registro de vacinação.
- Solicitação e atualização de exames com laboratório simulado.
- Cobranças e registro simplificado de pagamento.
- Notificações simuladas e auditoria de operações selecionadas.

## Modelo de domínio resumido
Tutor, Pet, Professional, Appointment, Medical record, Prescription, Product, Product lot, Stock movement, Vaccine application, Exam request, Invoice, Notification e Audit log.

## Design Patterns identificados no código

### 1. Strategy — comportamental
**Problema:** variar a regra de cálculo de preço.  
**Participantes:** `PricingStrategy`, `RegularPricing` e `EmergencyPricing` (acréscimo de 50%).  
**Benefício:** trocar a política sem espalhar condicionais.  
**Limite:** as estratégias existem e são testadas, mas não foram encontradas integradas ao fluxo atual de faturamento/API.

### 2. Factory / Simple Factory — criação
**Problema:** centralizar a criação de um canal de notificação simulado.  
**Participantes:** `NotificationFactory`, `NotificationChannel` e implementação lambda criada por `create(channel)`.  
**Benefício:** concentrar a criação do canal.  
**Limite:** retorna um canal simulado genérico; não há classes reais separadas de e-mail, SMS e mensagens instantâneas.

### 3. Observer — comportamental
**Problema:** comunicar eventos de consulta sem acoplar o serviço diretamente à auditoria.  
**Participantes:** `AppointmentObserver`, `AppointmentEventPublisher`, `AppointmentAuditObserver` e `AppointmentEvent`.  
**Fluxo:** `AppointmentService.save` publica o evento; o publisher notifica observers Spring; o observer grava a auditoria.  
**Limite:** alterações de status feitas diretamente no controller não passam pelo publisher.

### 4. Adapter — estrutural
**Problema:** normalizar resultado de laboratório para o formato interno.  
**Participantes:** `LaboratoryAdapter` e `BasicLaboratoryAdapter`.  
**Fluxo:** converte nulo em texto vazio e remove espaços externos.  
**Limite:** não há integração com laboratório real nem interpretação de formatos de fornecedores.

### 5. State — comportamental
**Problema:** restringir transições no ciclo de vida da consulta.  
**Participantes:** `AppointmentState`, `AppointmentStateFactory`, `AppointmentStatus` e endpoint de status em `ApiController`.  
**Transições:** AGENDADA → CONFIRMADA/CANCELADA/FALTOU; CONFIRMADA → EM_ATENDIMENTO/CANCELADA/FALTOU; EM_ATENDIMENTO → CONCLUIDA. CONCLUIDA, CANCELADA e FALTOU são finais.  
**Benefício:** centralizar a validação de transição.  
**Limite:** usa implementação anônima e tabela de transições, não classes de estado independentes.

### 6. Builder — criação
**Problema:** construir mensagem de notificação com validação.  
**Participantes:** `NotificationMessage` e `NotificationMessage.Builder`.  
**Fluxo:** `builder().channel(...).message(...).build()`; `build` rejeita mensagem vazia.  
**Limite:** builder simples para um objeto de mensagem.

## Testes
Resultado local relatado em 23/11/2026:
- `AppointmentStateFactoryTest`: 2.
- `PricingStrategyTest`: 2.
- `AppointmentServiceTest`: 4.
- Total: 8 testes, 0 falhas, 0 erros com `mvn clean test`.

A cobertura é parcial; não comprova todos os fluxos de estoque, vacinação, exames, financeiro, segurança ou integração.

## Limitações que devem ser declaradas
- Perfis existem, mas autorização fina por módulo ainda consta como limitação no README.
- Notificações e laboratório são simulados.
- Protocolos configuráveis de vacinação e lembretes automáticos não estão completos.
- Faturamento simplificado, sem orçamento detalhado, descontos, estorno ou gateway.
- Auditoria não guarda snapshots anteriores em todas as operações.
- Testes precisam ser ampliados.
- Validar execução com MySQL no ambiente de apresentação.

## Conclusão
Há uma base funcional e exemplos de seis padrões em categorias diferentes. A apresentação deve explicar o problema resolvido por cada padrão e reconhecer os limites reais, sem tratar simulações como integrações reais.
