package br.com.vetcare.repository;
import br.com.vetcare.model.Pet; import org.springframework.data.jpa.repository.JpaRepository;
public interface PetRepository extends JpaRepository<Pet,Long> {}
