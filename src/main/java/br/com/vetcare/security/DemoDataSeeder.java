package br.com.vetcare.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Insere dados fictícios apenas quando a base ainda não possui cadastros. */
@Component
public class DemoDataSeeder implements ApplicationRunner {
    private final JdbcTemplate db;

    public DemoDataSeeder(JdbcTemplate db) { this.db = db; }

    @Override
    public void run(ApplicationArguments args) {
        Integer alreadySeeded = db.queryForObject("SELECT COUNT(*) FROM audit_logs WHERE action=\'DEMO_SEED\'", Integer.class);
        if (alreadySeeded != null && alreadySeeded > 0) return;

        long tutor1 = insert("INSERT INTO tutores(nome,cpf,telefone,email,endereco) VALUES(?,?,?,?,?)",
                "Mariana Oliveira", "000.000.000-01", "(19) 98811-2201", "mariana.demo@example.com", "Rua das Acácias, 120 - Americana/SP");
        long tutor2 = insert("INSERT INTO tutores(nome,cpf,telefone,email,endereco) VALUES(?,?,?,?,?)",
                "Rafael Santos", "000.000.000-02", "(19) 98822-3302", "rafael.demo@example.com", "Av. Brasil, 450 - Americana/SP");
        long tutor3 = insert("INSERT INTO tutores(nome,cpf,telefone,email,endereco) VALUES(?,?,?,?,?)",
                "Camila Ferreira", "000.000.000-03", "(19) 98833-4403", "camila.demo@example.com", "Rua do Ipê, 75 - Santa Bárbara d'Oeste/SP");

        long pet1 = insert("INSERT INTO animais(nome,especie,raca,sexo,nascimento,peso,tutor_id) VALUES(?,?,?,?,?,?,?)",
                "Luna", "Cachorro", "Golden Retriever", "Fêmea", LocalDate.of(2021, 4, 12), 24.5, tutor1);
        long pet2 = insert("INSERT INTO animais(nome,especie,raca,sexo,nascimento,peso,tutor_id) VALUES(?,?,?,?,?,?,?)",
                "Mingau", "Gato", "SRD", "Macho", LocalDate.of(2022, 9, 3), 4.2, tutor2);
        long pet3 = insert("INSERT INTO animais(nome,especie,raca,sexo,nascimento,peso,tutor_id) VALUES(?,?,?,?,?,?,?)",
                "Thor", "Cachorro", "Shih-tzu", "Macho", LocalDate.of(2020, 1, 20), 7.1, tutor3);
        long pet4 = insert("INSERT INTO animais(nome,especie,raca,sexo,nascimento,peso,tutor_id) VALUES(?,?,?,?,?,?,?)",
                "Nina", "Gato", "Siamês", "Fêmea", LocalDate.of(2023, 6, 8), 3.6, tutor1);

        long vet1 = insert("INSERT INTO profissionais(nome,crmv,funcao,telefone) VALUES(?,?,?,?)",
                "Dra. Beatriz Almeida", "CRMV-SP 12345", "Veterinária", "(19) 98844-5501");
        long vet2 = insert("INSERT INTO profissionais(nome,crmv,funcao,telefone) VALUES(?,?,?,?)",
                "Dr. Lucas Martins", "CRMV-SP 23456", "Veterinário", "(19) 98855-6602");
        insert("INSERT INTO profissionais(nome,crmv,funcao,telefone) VALUES(?,?,?,?)",
                "Juliana Costa", "", "Recepcionista", "(19) 98866-7703");

        long appointment1 = insert("INSERT INTO consultas(data_hora,motivo,status,animal_id,profissional_id) VALUES(?,?,?,?,?)",
                LocalDateTime.now().minusDays(2).withHour(9).withMinute(0).withSecond(0).withNano(0), "Consulta de rotina e avaliação geral", "CONCLUIDA", pet1, vet1);
        insert("INSERT INTO consultas(data_hora,motivo,status,animal_id,profissional_id) VALUES(?,?,?,?,?)",
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(30).withSecond(0).withNano(0), "Retorno - acompanhamento", "CONFIRMADA", pet2, vet2);
        insert("INSERT INTO consultas(data_hora,motivo,status,animal_id,profissional_id) VALUES(?,?,?,?,?)",
                LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0), "Vacinação anual", "AGENDADA", pet3, vet1);
        insert("INSERT INTO consultas(data_hora,motivo,status,animal_id,profissional_id) VALUES(?,?,?,?,?)",
                LocalDateTime.now().minusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0), "Avaliação dermatológica", "CONCLUIDA", pet4, vet2);

        long record = insert("INSERT INTO medical_records(pet_id,professional_id,appointment_id,complaint,anamnesis,weight,temperature,diagnosis,conduct) VALUES(?,?,?,?,?,?,?,?,?)",
                pet1, vet1, appointment1, "Check-up anual", "Animal ativo, alimentação adequada e vacinação em dia.", 24.5, 38.5,
                "Paciente saudável, sem alterações relevantes", "Manter alimentação habitual e retorno anual.");
        db.update("INSERT INTO prescriptions(medical_record_id,medication,dosage,frequency,duration,route,instructions) VALUES(?,?,?,?,?,?,?)",
                record, "Suplemento vitamínico veterinário (exemplo)", "Conforme orientação profissional", "1 vez ao dia", "30 dias", "Oral", "Dado fictício para demonstração; seguir prescrição do veterinário responsável.");

        long product1 = insert("INSERT INTO products(name,category,sku,unit,minimum_stock) VALUES(?,?,?,?,?)",
                "Vacina V10 (demonstração)", "Vacinas", "DEMO-VAC-001", "dose", 5.0);
        long product2 = insert("INSERT INTO products(name,category,sku,unit,minimum_stock) VALUES(?,?,?,?,?)",
                "Antisséptico veterinário (demonstração)", "Higiene", "DEMO-HIG-002", "frasco", 3.0);
        long lot1 = insert("INSERT INTO product_lots(product_id,lot_number,expiry_date,quantity,unit_cost) VALUES(?,?,?,?,?)",
                product1, "LOTE-DEMO-V10", LocalDate.now().plusMonths(10), 12.0, 48.90);
        long lot2 = insert("INSERT INTO product_lots(product_id,lot_number,expiry_date,quantity,unit_cost) VALUES(?,?,?,?,?)",
                product2, "LOTE-DEMO-ANT", LocalDate.now().plusMonths(18), 2.0, 22.50);
        db.update("INSERT INTO stock_movements(lot_id,movement_type,quantity,reason) VALUES(?,?,?,?)", lot1, "ENTRADA", 12.0, "Estoque inicial fictício para demonstração");
        db.update("INSERT INTO stock_movements(lot_id,movement_type,quantity,reason) VALUES(?,?,?,?)", lot2, "ENTRADA", 2.0, "Estoque inicial fictício para demonstração");

        db.update("INSERT INTO vaccine_applications(pet_id,vaccine_name,application_date,lot_number,professional_id,next_dose,notes) VALUES(?,?,?,?,?,?,?)",
                pet1, "Vacina V10", LocalDate.now().minusMonths(11), "LOTE-V10-DEMO-2025", vet1, LocalDate.now().plusDays(30), "Registro fictício de demonstração.");
        db.update("INSERT INTO exam_requests(pet_id,appointment_id,exam_name,laboratory,status,result,result_at) VALUES(?,?,?,?,?,?,?)",
                pet1, appointment1, "Hemograma", "Laboratório Parceiro (simulado)", "RESULTADO_DISPONIVEL", "Resultado demonstrativo: parâmetros dentro dos valores de referência.", LocalDateTime.now().minusDays(2));
        db.update("INSERT INTO exam_requests(pet_id,exam_name,laboratory,status) VALUES(?,?,?,?)",
                pet2, "Exame parasitológico de fezes", "Laboratório Parceiro (simulado)", "EM_ANALISE");

        db.update("INSERT INTO invoices(tutor_id,appointment_id,description,total,status,payment_method,paid_at) VALUES(?,?,?,?,?,?,?)",
                tutor1, appointment1, "Consulta de rotina - demonstração", 180.00, "PAGA", "PIX", LocalDateTime.now().minusDays(2));
        db.update("INSERT INTO invoices(tutor_id,description,total,status) VALUES(?,?,?,?)",
                tutor2, "Consulta e retorno - demonstração", 120.00, "ABERTA");
        db.update("INSERT INTO notifications(tutor_id,channel,message,status) VALUES(?,?,?,?)",
                tutor1, "EMAIL", "Olá, Mariana! A consulta da Luna foi registrada. Esta é uma notificação simulada.", "SIMULADA");
        db.update("INSERT INTO notifications(tutor_id,channel,message,status) VALUES(?,?,?,?)",
                tutor2, "SMS", "Lembrete demonstrativo: retorno do Mingau agendado.", "SIMULADA");
        db.update("INSERT INTO audit_logs(username,action,entity_name,entity_id,details) VALUES(?,?,?,?,?)",
                "admin", "DEMO_SEED", "SISTEMA", null, "Carga inicial de dados fictícios de demonstração.");
    }

    private long insert(String sql, Object... values) {
        db.update(sql, values);
        Long id = db.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return id == null ? 0L : id;
    }
}
