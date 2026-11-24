package br.com.vetcare.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Entity @Table(name="consultas")
public class Appointment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotNull private LocalDateTime dataHora;
 private String motivo;
 @Enumerated(EnumType.STRING) private AppointmentStatus status=AppointmentStatus.AGENDADA;
 @ManyToOne(optional=false) @JoinColumn(name="animal_id") private Pet animal;
 @ManyToOne(optional=false) @JoinColumn(name="profissional_id") private Professional profissional;
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public LocalDateTime getDataHora(){return dataHora;} public void setDataHora(LocalDateTime dataHora){this.dataHora=dataHora;}
 public String getMotivo(){return motivo;} public void setMotivo(String motivo){this.motivo=motivo;}
 public AppointmentStatus getStatus(){return status;} public void setStatus(AppointmentStatus status){this.status=status;}
 public Pet getAnimal(){return animal;} public void setAnimal(Pet animal){this.animal=animal;}
 public Professional getProfissional(){return profissional;} public void setProfissional(Professional profissional){this.profissional=profissional;}
}
