# Diagramas UML — VETCARE 3D

Os diagramas representam a estrutura e os fluxos identificados no código. Mermaid é renderizado pelo GitHub em Markdown.

## Diagrama de classes
```mermaid
classDiagram
class Tutor
class Pet
class Professional
class Appointment {
  Long id
  LocalDateTime dataHora
  String motivo
  AppointmentStatus status
}
class AppointmentService
class AppointmentRepository
class AppointmentState {
  <<interface>>
  canMoveTo(next)
}
class AppointmentStateFactory
class AppointmentEventPublisher
class AppointmentObserver {
  <<interface>>
  onEvent(event)
}
class AppointmentAuditObserver
class PricingStrategy {
  <<interface>>
  calculate(base)
}
class RegularPricing
class EmergencyPricing
class NotificationFactory
class NotificationChannel {
  <<interface>>
  send(destination,message)
}
class NotificationMessage
class NotificationMessageBuilder
class LaboratoryAdapter {
  <<interface>>
  normalizeResult(payload)
}
class BasicLaboratoryAdapter
Tutor "1" --> "*" Pet
Pet "1" --> "*" Appointment
Professional "1" --> "*" Appointment
AppointmentService --> AppointmentRepository
AppointmentService --> AppointmentEventPublisher
AppointmentStateFactory ..> AppointmentState
AppointmentEventPublisher --> AppointmentObserver
AppointmentObserver <|.. AppointmentAuditObserver
PricingStrategy <|.. RegularPricing
PricingStrategy <|.. EmergencyPricing
NotificationFactory ..> NotificationChannel
NotificationMessage *-- NotificationMessageBuilder
LaboratoryAdapter <|.. BasicLaboratoryAdapter
```

## Ciclo de vida de consulta
```mermaid
stateDiagram-v2
[*] --> AGENDADA
AGENDADA --> CONFIRMADA
AGENDADA --> CANCELADA
AGENDADA --> FALTOU
CONFIRMADA --> EM_ATENDIMENTO
CONFIRMADA --> CANCELADA
CONFIRMADA --> FALTOU
EM_ATENDIMENTO --> CONCLUIDA
CANCELADA --> [*]
FALTOU --> [*]
CONCLUIDA --> [*]
```

## Sequência — agendar consulta
```mermaid
sequenceDiagram
actor Usuario
participant API as ApiController
participant Service as AppointmentService
participant Repo as AppointmentRepository
participant Publisher as AppointmentEventPublisher
participant Observer as AppointmentAuditObserver
Usuario->>API: POST /api/consultas
API->>Service: save(consulta)
Service->>Service: validar profissional e data/hora
Service->>Repo: verificar conflito de horário
Repo-->>Service: horário livre
Service->>Repo: save(consulta)
Repo-->>Service: consulta salva
Service->>Publisher: publish(evento)
Publisher->>Observer: onEvent(evento)
Observer->>Observer: gravar auditoria
Service-->>API: consulta salva
API-->>Usuario: resposta HTTP
```

## Sequência — alterar status
```mermaid
sequenceDiagram
actor Usuario
participant API as ApiController
participant Repo as AppointmentRepository
participant Factory as AppointmentStateFactory
participant State as AppointmentState
Usuario->>API: PATCH /api/consultas/{id}/status
API->>Repo: findById(id)
Repo-->>API: consulta atual
API->>Factory: from(statusAtual)
Factory-->>API: estado atual
API->>State: canMoveTo(novoStatus)
alt transição permitida
State-->>API: true
API->>Repo: save(consulta)
API-->>Usuario: consulta atualizada
else transição inválida
State-->>API: false
API-->>Usuario: HTTP 400
end
```

## Sequência — notificação simulada
```mermaid
sequenceDiagram
actor Usuario
participant Controller as ClinicModulesController
participant Builder as NotificationMessage.Builder
participant Factory as NotificationFactory
participant Channel as NotificationChannel
participant DB as MySQL
Usuario->>Controller: POST /api/notificacoes
Controller->>Builder: channel(...), message(...), build()
Builder-->>Controller: mensagem validada
Controller->>Factory: create(channel)
Factory-->>Controller: canal simulado
Controller->>Channel: send(destino,mensagem)
Channel-->>Controller: prévia simulada
Controller->>DB: inserir notificação
Controller-->>Usuario: confirmação
```

## Observação
O requisito de múltiplos tutores por animal não está demonstrado no modelo JPA consultado; tratar como limitação até implementação e validação.
