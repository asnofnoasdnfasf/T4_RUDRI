package backend.demo.repository;

import backend.demo.model.pedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface pedidoRepository extends JpaRepository<pedidoModel, Integer> {
    List<pedidoModel> findByEstadoIn(List<String> estados);
}
