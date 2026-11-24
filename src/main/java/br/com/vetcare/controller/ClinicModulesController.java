package br.com.vetcare.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import br.com.vetcare.patterns.NotificationFactory;
import br.com.vetcare.patterns.NotificationMessage;
import br.com.vetcare.patterns.BasicLaboratoryAdapter;
@RestController @RequestMapping("/api")
public class ClinicModulesController {
 private final JdbcTemplate db;
 public ClinicModulesController(JdbcTemplate db){this.db=db;}
 private String username(HttpSession s){return String.valueOf(s.getAttribute("USERNAME"));}
 private void audit(HttpSession s,String action,String entity,Long id,String details){db.update("INSERT INTO audit_logs(username,action,entity_name,entity_id,details) VALUES(?,?,?,?,?)",username(s),action,entity,id,details);}
 private Map<String,Object> one(String sql,Object...args){return db.queryForMap(sql,args);}
 private List<Map<String,Object>> all(String sql,Object...args){return db.queryForList(sql,args);}
 private ResponseEntity<?> fail(String msg){return ResponseEntity.badRequest().body(Map.of("erro",msg));}

 // Prontuário e prescrições
 @GetMapping("/prontuarios") public List<Map<String,Object>> records(){return all("SELECT r.*,p.nome animal,pr.nome profissional FROM medical_records r JOIN animais p ON p.id=r.pet_id JOIN profissionais pr ON pr.id=r.professional_id ORDER BY r.created_at DESC");}
 @PostMapping("/prontuarios") public ResponseEntity<?> createRecord(@RequestBody Map<String,Object>b,HttpSession s){
  try{db.update("INSERT INTO medical_records(pet_id,professional_id,appointment_id,complaint,anamnesis,weight,temperature,diagnosis,conduct) VALUES(?,?,?,?,?,?,?,?,?)",
   num(b,"petId"),num(b,"professionalId"),numNullable(b,"appointmentId"),str(b,"complaint"),str(b,"anamnesis"),dbl(b,"weight"),dbl(b,"temperature"),str(b,"diagnosis"),str(b,"conduct"));
   Long id=db.queryForObject("SELECT LAST_INSERT_ID()",Long.class);audit(s,"CREATE","PRONTUARIO",id,"Prontuário criado");return ResponseEntity.ok(Map.of("id",id));
  }catch(Exception e){return fail("Confira animal, profissional e os dados clínicos.");}
 }
 @GetMapping("/prontuarios/{id}/prescricoes") public List<Map<String,Object>> prescriptions(@PathVariable Long id){return all("SELECT * FROM prescriptions WHERE medical_record_id=? ORDER BY created_at DESC",id);}
 @PostMapping("/prontuarios/{id}/prescricoes") public ResponseEntity<?> prescription(@PathVariable Long id,@RequestBody Map<String,Object>b,HttpSession s){
  try{db.update("INSERT INTO prescriptions(medical_record_id,medication,dosage,frequency,duration,route,instructions) VALUES(?,?,?,?,?,?,?)",id,str(b,"medication"),str(b,"dosage"),str(b,"frequency"),str(b,"duration"),str(b,"route"),str(b,"instructions"));audit(s,"CREATE","PRESCRICAO",id,str(b,"medication"));return ResponseEntity.ok(Map.of("mensagem","Prescrição registrada."));}catch(Exception e){return fail("Não foi possível salvar a prescrição.");}
 }

 // Produtos, lotes e movimentações. Saldo é atualizado dentro de transação SQL.
 @GetMapping("/estoque/produtos") public List<Map<String,Object>> products(){return all("SELECT p.*,COALESCE((SELECT SUM(l.quantity) FROM product_lots l WHERE l.product_id=p.id),0) stock FROM products p ORDER BY p.name");}
 @PostMapping("/estoque/produtos") public ResponseEntity<?> product(@RequestBody Map<String,Object>b,HttpSession s){
  try{db.update("INSERT INTO products(name,category,sku,unit,minimum_stock) VALUES(?,?,?,?,?)",str(b,"name"),str(b,"category"),str(b,"sku"),or(str(b,"unit"),"un"),or(dbl(b,"minimumStock"),0d));audit(s,"CREATE","PRODUTO",null,str(b,"name"));return ResponseEntity.ok(Map.of("mensagem","Produto cadastrado."));}catch(Exception e){return fail("Produto inválido ou SKU já cadastrado.");}
 }
 @PostMapping("/estoque/lotes") public ResponseEntity<?> lot(@RequestBody Map<String,Object>b,HttpSession s){
  try{db.update("INSERT INTO product_lots(product_id,lot_number,expiry_date,quantity,unit_cost) VALUES(?,?,?,?,?)",num(b,"productId"),str(b,"lotNumber"),b.get("expiryDate"),or(dbl(b,"quantity"),0d),or(dbl(b,"unitCost"),0d));audit(s,"CREATE","LOTE",null,str(b,"lotNumber"));return ResponseEntity.ok(Map.of("mensagem","Lote cadastrado."));}catch(Exception e){return fail("Confira produto, lote, validade e quantidade.");}
 }
 @GetMapping("/estoque/lotes") public List<Map<String,Object>> lots(){return all("SELECT l.*,p.name product_name FROM product_lots l JOIN products p ON p.id=l.product_id ORDER BY l.expiry_date");}
 @Transactional @PostMapping("/estoque/movimentacoes") public ResponseEntity<?> movement(@RequestBody Map<String,Object>b,HttpSession s){
  try{
   Long lotId=num(b,"lotId");Double qty=dbl(b,"quantity");String type=str(b,"type");
   if(qty==null||qty<=0||!("ENTRADA".equals(type)||"SAIDA".equals(type)))return fail("Tipo ou quantidade inválida.");
   Map<String,Object> lot=one("SELECT quantity FROM product_lots WHERE id=? FOR UPDATE",lotId);double current=((Number)lot.get("quantity")).doubleValue();
   if("SAIDA".equals(type)&&current<qty)return fail("Estoque insuficiente para esta saída.");
   double updated="ENTRADA".equals(type)?current+qty:current-qty;
   db.update("UPDATE product_lots SET quantity=? WHERE id=?",updated,lotId);
   db.update("INSERT INTO stock_movements(lot_id,movement_type,quantity,reason) VALUES(?,?,?,?)",lotId,type,qty,str(b,"reason"));
   audit(s,"STOCK_"+type,"LOTE",lotId,"Quantidade: "+qty);return ResponseEntity.ok(Map.of("saldo",updated));
  }catch(Exception e){return fail("Não foi possível movimentar o estoque.");}
 }
 @GetMapping("/estoque/movimentacoes") public List<Map<String,Object>> movements(){return all("SELECT m.*,l.lot_number,p.name product_name FROM stock_movements m JOIN product_lots l ON l.id=m.lot_id JOIN products p ON p.id=l.product_id ORDER BY m.created_at DESC");}
 @GetMapping("/estoque/alertas") public List<Map<String,Object>> stockAlerts(){return all("SELECT p.id,p.name,p.minimum_stock,COALESCE(SUM(l.quantity),0) stock FROM products p LEFT JOIN product_lots l ON l.product_id=p.id GROUP BY p.id,p.name,p.minimum_stock HAVING stock<=p.minimum_stock");}

 // Vacinas, exames e faturamento
 @GetMapping("/vacinas") public List<Map<String,Object>> vaccines(){return all("SELECT v.*,p.nome animal,pr.nome profissional FROM vaccine_applications v JOIN animais p ON p.id=v.pet_id JOIN profissionais pr ON pr.id=v.professional_id ORDER BY v.application_date DESC");}
 @PostMapping("/vacinas") public ResponseEntity<?> vaccine(@RequestBody Map<String,Object>b,HttpSession s){
  try{db.update("INSERT INTO vaccine_applications(pet_id,vaccine_name,application_date,lot_number,professional_id,next_dose,notes) VALUES(?,?,?,?,?,?,?)",num(b,"petId"),str(b,"vaccineName"),b.get("applicationDate"),str(b,"lotNumber"),num(b,"professionalId"),b.get("nextDose"),str(b,"notes"));audit(s,"CREATE","VACINA",null,str(b,"vaccineName"));return ResponseEntity.ok(Map.of("mensagem","Vacina registrada."));}catch(Exception e){return fail("Confira os dados da aplicação.");}
 }
 @GetMapping("/exames") public List<Map<String,Object>> exams(){return all("SELECT e.*,p.nome animal FROM exam_requests e JOIN animais p ON p.id=e.pet_id ORDER BY e.requested_at DESC");}
 @PostMapping("/exames") public ResponseEntity<?> exam(@RequestBody Map<String,Object>b,HttpSession s){
  try{db.update("INSERT INTO exam_requests(pet_id,appointment_id,exam_name,laboratory,status) VALUES(?,?,?,?,?)",num(b,"petId"),numNullable(b,"appointmentId"),str(b,"examName"),str(b,"laboratory"),"SOLICITADO");audit(s,"CREATE","EXAME",null,str(b,"examName"));return ResponseEntity.ok(Map.of("mensagem","Exame solicitado."));}catch(Exception e){return fail("Confira os dados do exame.");}
 }
 @PatchMapping("/exames/{id}") public ResponseEntity<?> examResult(@PathVariable Long id,@RequestBody Map<String,Object>b,HttpSession s){
  String normalizedResult=new BasicLaboratoryAdapter().normalizeResult(str(b,"result"));String status=or(str(b,"status"),"RESULTADO_DISPONIVEL");if(!List.of("SOLICITADO","EM_ANALISE","RESULTADO_DISPONIVEL","CANCELADO").contains(status))return fail("Status inválido.");
  db.update("UPDATE exam_requests SET status=?,result=?,result_at=IF(?='RESULTADO_DISPONIVEL',NOW(),result_at) WHERE id=?",status,normalizedResult,status,id);audit(s,"UPDATE","EXAME",id,status);return ResponseEntity.ok(Map.of("mensagem","Exame atualizado."));
 }
 @GetMapping("/financeiro") public List<Map<String,Object>> invoices(){return all("SELECT i.*,t.nome tutor FROM invoices i JOIN tutores t ON t.id=i.tutor_id ORDER BY i.created_at DESC");}
 @PostMapping("/financeiro") public ResponseEntity<?> invoice(@RequestBody Map<String,Object>b,HttpSession s){
  try{Double total=dbl(b,"total");if(total==null||total<0)return fail("Valor total inválido.");db.update("INSERT INTO invoices(tutor_id,appointment_id,description,total,status) VALUES(?,?,?,?,?)",num(b,"tutorId"),numNullable(b,"appointmentId"),str(b,"description"),total,"ABERTA");audit(s,"CREATE","FATURA",null,str(b,"description"));return ResponseEntity.ok(Map.of("mensagem","Cobrança criada."));}catch(Exception e){return fail("Confira tutor, descrição e valor.");}
 }
 @PatchMapping("/financeiro/{id}/pagar") public ResponseEntity<?> pay(@PathVariable Long id,@RequestBody Map<String,String>b,HttpSession s){
  String method=b.getOrDefault("paymentMethod","OUTRO");db.update("UPDATE invoices SET status='PAGA',payment_method=?,paid_at=NOW() WHERE id=?",method,id);audit(s,"PAYMENT","FATURA",id,method);return ResponseEntity.ok(Map.of("mensagem","Pagamento registrado."));
 }

 // Comunicação simulada e auditoria
 @GetMapping("/notificacoes") public List<Map<String,Object>> notifications(){return all("SELECT n.*,t.nome tutor FROM notifications n LEFT JOIN tutores t ON t.id=n.tutor_id ORDER BY n.created_at DESC");}
 @PostMapping("/notificacoes") public ResponseEntity<?> notification(@RequestBody Map<String,Object>b,HttpSession s){
  try{NotificationMessage note=NotificationMessage.builder().channel(or(str(b,"channel"),"INTERNA")).message(str(b,"message")).build();String simulated=new NotificationFactory().create(note.channel()).send("tutor",note.message());db.update("INSERT INTO notifications(tutor_id,channel,message,status) VALUES(?,?,?,?)",numNullable(b,"tutorId"),note.channel(),note.message(),"SIMULADA");audit(s,"CREATE","NOTIFICACAO",null,simulated);return ResponseEntity.ok(Map.of("mensagem","Notificação registrada como simulada.","preview",simulated));}catch(Exception e){return fail("Informe uma mensagem.");}
 }
 @GetMapping("/auditoria") public List<Map<String,Object>> auditLogs(){return all("SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT 500");}
 private String str(Map<String,Object>b,String k){Object v=b.get(k);return v==null?null:String.valueOf(v);}
 private String or(String v,String fallback){return v==null||v.isBlank()?fallback:v;}
 private Double dbl(Map<String,Object>b,String k){Object v=b.get(k);if(v==null||String.valueOf(v).isBlank())return null;return Double.valueOf(String.valueOf(v));}
 private Double or(Double v,Double f){return v==null?f:v;}
 private Long num(Map<String,Object>b,String k){Object v=b.get(k);return Long.valueOf(String.valueOf(v));}
 private Long numNullable(Map<String,Object>b,String k){Object v=b.get(k);return v==null||String.valueOf(v).isBlank()?null:Long.valueOf(String.valueOf(v));}
}
