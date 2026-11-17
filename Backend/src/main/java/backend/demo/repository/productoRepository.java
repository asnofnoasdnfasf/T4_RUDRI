package backend.demo.repository;

import backend.demo.model.productoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface productoRepository extends JpaRepository<productoModel, Integer> {
    List<productoModel> findByDisponibleTrue();
    boolean existsByNombreIgnoreCase(String nombre);
}
