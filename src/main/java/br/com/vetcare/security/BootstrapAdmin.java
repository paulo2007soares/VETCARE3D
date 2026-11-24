package br.com.vetcare.security;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class BootstrapAdmin implements ApplicationRunner {
 private final JdbcTemplate jdbc; private final PasswordEncoder encoder;
 public BootstrapAdmin(JdbcTemplate jdbc,PasswordEncoder encoder){this.jdbc=jdbc;this.encoder=encoder;}
 @Override public void run(ApplicationArguments args){
  Integer count=jdbc.queryForObject("SELECT COUNT(*) FROM app_users",Integer.class);
  if(count!=null && count==0) jdbc.update("INSERT INTO app_users(nome,username,password_hash,role) VALUES(?,?,?,?)",
   "Administrador VETCARE","admin",encoder.encode("Admin@12345"),"ADMIN");
 }
}
