# Roteiro de apresentação — VETCARE 3D

## Abertura
"Este é o VETCARE 3D, sistema web de apoio à gestão de uma clínica veterinária, desenvolvido com Java 21, Spring Boot, Spring Data JPA, MySQL e interface web."

## Demonstração
1. Iniciar MySQL no XAMPP e executar backend.
2. Abrir `http://localhost:8080/login.html`.
3. Entrar com administrador configurado.
4. Mostrar painel e cadastros de tutores, animais e profissionais.
5. Criar consulta e mostrar validação de conflito.
6. Alterar status e mostrar transições permitidas.
7. Mostrar módulos de prontuário, estoque, vacina, exame, financeiro, notificação e auditoria, conforme disponíveis.

## Três padrões para demonstrar
- **State:** endpoint `PATCH /api/consultas/{id}/status`; consulta concluída não retorna ao atendimento.
- **Observer:** `AppointmentService` publica evento ao salvar; `AppointmentAuditObserver` registra o evento.
- **Builder:** `NotificationMessage.Builder` monta mensagem e rejeita conteúdo vazio.

Também é possível explicar Strategy (políticas de preço), Factory (canal simulado) e Adapter (normalização do resultado de exame). Ressalva: Strategy ainda não está conectada ao faturamento.

## Testes
Executar no terminal:
```powershell
mvn clean test
```
Resultado relatado em 23/11/2026: 8 testes, 0 falhas e 0 erros. Repetir antes da apresentação.

## Perguntas frequentes
**As notificações são reais?** Não, são simuladas e registradas no banco.  
**Há integração com laboratórios?** Não, há normalização básica, sem fornecedor externo.  
**Os perfis têm permissões completas?** Não; a autorização fina por módulo ainda consta como limitação.  
**Por que usar padrões?** Para isolar variações e responsabilidades e reduzir acoplamento.

## Checklist
- [ ] Executar `mvn clean test`.
- [ ] Iniciar MySQL e validar login e fluxos principais.
- [ ] Confirmar banco de demonstração populado.
- [ ] Alterar credenciais padrão antes de usar dados reais.
- [ ] Conferir `application.properties`; não publicar senhas reais.
- [ ] Sincronizar a cópia local com GitHub.
- [ ] Ensaiar três padrões sem afirmar que simulações são integrações reais.
