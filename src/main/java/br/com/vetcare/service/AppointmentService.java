package br.com.vetcare.service;
import br.com.vetcare.model.*; import br.com.vetcare.repository.AppointmentRepository;
import br.com.vetcare.patterns.*; import org.springframework.stereotype.Service;
@Service
public class AppointmentService {
 private final AppointmentRepository repository; private final AppointmentEventPublisher publisher;
 public AppointmentService(AppointmentRepository repository,AppointmentEventPublisher publisher){this.repository=repository;this.publisher=publisher;}
 public Appointment save(Appointment a){
  if(a.getProfissional()==null || a.getDataHora()==null) throw new IllegalArgumentException("Informe profissional e data/hora.");
  Long id=a.getId()==null ? -1L : a.getId();
  if(repository.existsByProfissionalIdAndDataHoraAndStatusNotAndIdNot(a.getProfissional().getId(),a.getDataHora(),AppointmentStatus.CANCELADA,id))
   throw new IllegalArgumentException("Este profissional já possui uma consulta nesse horário.");
  Appointment saved=repository.save(a);publisher.publish(new AppointmentEvent(saved.getId(),"Consulta salva: "+saved.getStatus()));return saved;
 }
}
