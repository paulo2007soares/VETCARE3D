package br.com.vetcare.repository;
import br.com.vetcare.model.Professional; import org.springframework.data.jpa.repository.JpaRepository;
public interface ProfessionalRepository extends JpaRepository<Professional,Long> {}
