package br.com.vetcare.repository;
import br.com.vetcare.model.Tutor; import org.springframework.data.jpa.repository.JpaRepository;
public interface TutorRepository extends JpaRepository<Tutor,Long> { boolean existsByCpfAndIdNot(String cpf, Long id); }
