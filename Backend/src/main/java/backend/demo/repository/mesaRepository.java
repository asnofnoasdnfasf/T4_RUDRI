package backend.demo.repository;

import backend.demo.model.mesaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface mesaRepository extends JpaRepository<mesaModel, Integer> {
    boolean existsByNumero(Integer numero);
    Optional<mesaModel> findByNumero(Integer numero);
}
