package br.com.vetcare.repository;
import br.com.vetcare.model.*; import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
 boolean existsByProfissionalIdAndDataHoraAndStatusNotAndIdNot(Long profissionalId, LocalDateTime dataHora, AppointmentStatus status, Long id);
}
