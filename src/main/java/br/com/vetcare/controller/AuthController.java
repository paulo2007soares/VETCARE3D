package br.com.vetcare.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final JdbcTemplate jdbc; private final PasswordEncoder encoder;
 public AuthController(JdbcTemplate jdbc,PasswordEncoder encoder){this.jdbc=jdbc;this.encoder=encoder;}
 @PostMapping("/login") public ResponseEntity<?> login(@RequestBody Map<String,String> body,HttpSession session){
  String username=body.getOrDefault("username","").trim(), password=body.getOrDefault("password","");
  List<Map<String,Object>> rows=jdbc.queryForList("SELECT id,nome,username,password_hash,role,active FROM app_users WHERE username=?",username);
  if(rows.isEmpty() || !Boolean.TRUE.equals(rows.get(0).get("active")) || !encoder.matches(password,(String)rows.get(0).get("password_hash")))
   return ResponseEntity.status(401).body(Map.of("erro","Usuário ou senha inválidos."));
  Map<String,Object> u=rows.get(0); session.setAttribute("USER_ID",u.get("id"));session.setAttribute("USERNAME",u.get("username"));session.setAttribute("ROLE",u.get("role"));
  jdbc.update("INSERT INTO audit_logs(username,action,entity_name,details) VALUES(?,?,?,?)",username,"LOGIN","AUTH","Login realizado");
  return ResponseEntity.ok(Map.of("id",u.get("id"),"nome",u.get("nome"),"username",u.get("username"),"role",u.get("role")));
 }
 @GetMapping("/me") public ResponseEntity<?> me(HttpSession session){
  if(session.getAttribute("USER_ID")==null)return ResponseEntity.status(401).body(Map.of("autenticado",false));
  return ResponseEntity.ok(Map.of("autenticado",true,"id",session.getAttribute("USER_ID"),"username",session.getAttribute("USERNAME"),"role",session.getAttribute("ROLE")));
 }
 @PostMapping("/logout") public Map<String,String> logout(HttpSession session){session.invalidate();return Map.of("mensagem","Logout realizado.");}
 @PostMapping("/users") public ResponseEntity<?> createUser(@RequestBody Map<String,String> b,HttpSession session){
  if(!"ADMIN".equals(session.getAttribute("ROLE")))return ResponseEntity.status(403).body(Map.of("erro","Somente administrador pode criar usuários."));
  String nome=b.getOrDefault("nome","").trim(), username=b.getOrDefault("username","").trim(), password=b.getOrDefault("password","");
  String role=b.getOrDefault("role","RECEPTION");
  if(nome.isBlank()||username.isBlank()||password.length()<8)return ResponseEntity.badRequest().body(Map.of("erro","Informe nome, usuário e senha com pelo menos 8 caracteres."));
  if(!List.of("ADMIN","VET","RECEPTION","STOCK","FINANCE").contains(role))return ResponseEntity.badRequest().body(Map.of("erro","Perfil inválido."));
  try{jdbc.update("INSERT INTO app_users(nome,username,password_hash,role) VALUES(?,?,?,?)",nome,username,encoder.encode(password),role);}
  catch(Exception e){return ResponseEntity.badRequest().body(Map.of("erro","Não foi possível criar. Verifique se o usuário já existe."));}
  return ResponseEntity.ok(Map.of("mensagem","Usuário criado."));
 }
 @GetMapping("/users") public ResponseEntity<?> users(HttpSession session){
  if(!"ADMIN".equals(session.getAttribute("ROLE")))return ResponseEntity.status(403).body(Map.of("erro","Acesso restrito ao administrador."));
  return ResponseEntity.ok(jdbc.queryForList("SELECT id,nome,username,role,active,created_at FROM app_users ORDER BY nome"));
 }
}
