package br.com.vetcare.patterns;
import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Component;
@Component
public class AppointmentAuditObserver implements AppointmentObserver {
 private final JdbcTemplate jdbc;
 public AppointmentAuditObserver(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public void onEvent(AppointmentEvent event){jdbc.update("INSERT INTO audit_logs(username,action,entity_name,entity_id,details) VALUES(?,?,?,?,?)","system","APPOINTMENT_EVENT","CONSULTA",event.appointmentId(),event.event());}
}
