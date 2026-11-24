package br.com.vetcare.controller;
import br.com.vetcare.model.*; import br.com.vetcare.repository.*; import br.com.vetcare.service.AppointmentService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.util.*;
import br.com.vetcare.patterns.AppointmentStateFactory;
@RestController @RequestMapping("/api")
@CrossOrigin(origins="*")
public class ApiController {
 private final TutorRepository tutores; private final PetRepository animais; private final ProfessionalRepository profissionais;
 private final AppointmentRepository consultas; private final AppointmentService agenda;
 public ApiController(TutorRepository t, PetRepository a, ProfessionalRepository p, AppointmentRepository c, AppointmentService s){tutores=t;animais=a;profissionais=p;consultas=c;agenda=s;}
 @GetMapping("/dashboard") public Map<String,Long> dashboard(){return Map.of("tutores",tutores.count(),"animais",animais.count(),"profissionais",profissionais.count(),"consultas",consultas.count());}
 @GetMapping("/tutores") public List<Tutor> tutores(){return tutores.findAll();}
 @PostMapping("/tutores") public Tutor criarTutor(@Valid @RequestBody Tutor t){return tutores.save(t);}
 @PutMapping("/tutores/{id}") public Tutor editarTutor(@PathVariable Long id,@Valid @RequestBody Tutor t){t.setId(id);return tutores.save(t);}
 @DeleteMapping("/tutores/{id}") public ResponseEntity<?> excluirTutor(@PathVariable Long id){tutores.deleteById(id);return ResponseEntity.noContent().build();}
 @GetMapping("/animais") public List<Pet> animais(){return animais.findAll();}
 @PostMapping("/animais") public Pet criarAnimal(@Valid @RequestBody Pet p){return animais.save(p);}
 @PutMapping("/animais/{id}") public Pet editarAnimal(@PathVariable Long id,@Valid @RequestBody Pet p){p.setId(id);return animais.save(p);}
 @DeleteMapping("/animais/{id}") public ResponseEntity<?> excluirAnimal(@PathVariable Long id){animais.deleteById(id);return ResponseEntity.noContent().build();}
 @GetMapping("/profissionais") public List<Professional> profissionais(){return profissionais.findAll();}
 @PostMapping("/profissionais") public Professional criarProfissional(@Valid @RequestBody Professional p){return profissionais.save(p);}
 @PutMapping("/profissionais/{id}") public Professional editarProfissional(@PathVariable Long id,@Valid @RequestBody Professional p){p.setId(id);return profissionais.save(p);}
 @DeleteMapping("/profissionais/{id}") public ResponseEntity<?> excluirProfissional(@PathVariable Long id){profissionais.deleteById(id);return ResponseEntity.noContent().build();}
 @GetMapping("/consultas") public List<Appointment> consultas(){return consultas.findAll();}
 @PostMapping("/consultas") public Appointment criarConsulta(@RequestBody Appointment a){return agenda.save(a);}
 @PutMapping("/consultas/{id}") public Appointment editarConsulta(@PathVariable Long id,@RequestBody Appointment a){a.setId(id);return agenda.save(a);}
 @PatchMapping("/consultas/{id}/status") public Appointment status(@PathVariable Long id,@RequestBody Map<String,String> body){Appointment a=consultas.findById(id).orElseThrow();AppointmentStatus next=AppointmentStatus.valueOf(body.get("status"));if(!new AppointmentStateFactory().from(a.getStatus()).canMoveTo(next))throw new IllegalArgumentException("Transição de status não permitida: "+a.getStatus()+" → "+next);a.setStatus(next);return consultas.save(a);}
 @DeleteMapping("/consultas/{id}") public ResponseEntity<?> excluirConsulta(@PathVariable Long id){consultas.deleteById(id);return ResponseEntity.noContent().build();}
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,String>> badRequest(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("erro",e.getMessage()));}
}
