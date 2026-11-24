# VETCARE 3D — versão inicial funcional

Projeto inicial baseado no enunciado enviado. Stack: Java 21, Spring Boot 3.4.5, Spring Data JPA, MySQL (XAMPP) e interface web HTML/CSS/JS.

## O que já funciona
- Painel com contagem de tutores, animais, profissionais e consultas.
- CRUD de tutores.
- CRUD de animais vinculados a tutores.
- CRUD de profissionais.
- CRUD de consultas vinculadas a animal e profissional.
- Regra básica que impede duas consultas no mesmo horário para o mesmo profissional (consultas canceladas não bloqueiam).
- Mudança de status da consulta pela API.

## Requisitos
- JDK 21 instalado.
- Maven instalado (ou usar o Maven Wrapper, se adicionar um).
- XAMPP com MySQL iniciado.
- VS Code com Extension Pack for Java (recomendado).

## Como executar
1. Extraia a pasta `VETCARE3D`.
2. Abra o XAMPP e inicie **MySQL**.
3. Abra `http://localhost/phpmyadmin`.
4. Crie um banco chamado `vetcare3d` com collation `utf8mb4_unicode_ci`. (A aplicação também usa `createDatabaseIfNotExist`, mas criar pelo phpMyAdmin evita dúvidas de permissão.)
5. Abra a pasta do projeto no VS Code.
6. No terminal integrado, dentro da pasta que contém `pom.xml`, execute:
   - Windows: `mvn spring-boot:run`
7. Aguarde a mensagem de que o servidor iniciou.
8. Acesse `http://localhost:8080`.

## Configuração do MySQL
Padrão configurado para XAMPP:
- host: `localhost`
- porta: `3306`
- banco: `vetcare3d`
- usuário: `root`
- senha: vazia

Se seu MySQL tiver outra senha, execute no PowerShell antes de iniciar:
`$env:DB_PASSWORD="SUA_SENHA"; mvn spring-boot:run`

Se a porta 3306 estiver ocupada, ajuste a porta no painel do XAMPP e em `DB_URL`.

## API disponível
- `GET/POST /api/tutores`
- `PUT/DELETE /api/tutores/{id}`
- `GET/POST /api/animais`
- `PUT/DELETE /api/animais/{id}`
- `GET/POST /api/profissionais`
- `PUT/DELETE /api/profissionais/{id}`
- `GET/POST /api/consultas`
- `PUT/DELETE /api/consultas/{id}`
- `PATCH /api/consultas/{id}/status` com JSON `{"status":"CONFIRMADA"}`
- `GET /api/dashboard`

## Importante
Esta é uma primeira entrega executável/MVP, não a entrega acadêmica completa do enunciado. Ainda precisam ser implementados e testados: autenticação e permissões, prontuário e histórico clínico, prescrições, lotes/estoque, vacinação, exames, faturamento, notificações, auditoria, documentação UML, seis padrões de projeto justificados e testes unitários JUnit. Não exponha o servidor à internet nem use dados reais de pacientes até implementar segurança, autorização, validação e backup adequados.

## Se der erro
Copie o erro completo do terminal e envie. Não altere vários arquivos de uma vez: vamos corrigir uma etapa por vez.

## Dados fictícios de demonstração
Na primeira inicialização desta versão, o sistema inclui automaticamente registros fictícios para demonstrar os módulos: tutores, pets, profissionais, consultas, prontuário, prescrição, produtos, lotes, movimentações, vacinação, exames, cobranças, notificações e auditoria. A carga é executada apenas uma vez por banco, identificada pelo evento `DEMO_SEED` na auditoria; ela não apaga nem substitui os dados existentes.

Os CPFs, telefones, e-mails e demais informações da demonstração são inventados e não devem ser usados como dados reais. Se o banco já tiver sido inicializado com a carga de demonstração, ela não será repetida automaticamente.
