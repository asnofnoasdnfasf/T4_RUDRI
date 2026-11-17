package backend.demo.controller;

import backend.demo.model.mesaModel;
import backend.demo.repository.mesaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class mesaController {

    private final mesaRepository mesaRepo;

    public mesaController(mesaRepository mesaRepo) {
        this.mesaRepo = mesaRepo;
    }

    @GetMapping
    public List<mesaModel> listarMesas() {
        return mesaRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearMesa(@RequestBody(required = false) mesaModel nueva) {
        if (nueva == null || nueva.getNumero() == null || nueva.getNumero() <= 0) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "El número de mesa debe ser un entero positivo.")
            );
        }
        int capacidad = nueva.getCapacidad() != null ? nueva.getCapacidad() : 4;
        if (capacidad <= 0 || capacidad > 50) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "La capacidad de la mesa debe estar entre 1 y 50 personas.")
            );
        }
        if (mesaRepo.existsByNumero(nueva.getNumero())) {
            return ResponseEntity.status(409).body(
                Map.of("mensaje", "Ya existe una mesa registrada con ese número.")
            );
        }

        nueva.setCapacidad(capacidad);
        if (nueva.getActiva() == null) {
            nueva.setActiva(true);
        }
        mesaModel creada = mesaRepo.save(nueva);
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMesa(
            @PathVariable Integer id,
            @RequestBody(required = false) mesaModel datos) {
        if (datos == null || datos.getNumero() == null || datos.getNumero() <= 0) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "El número de mesa debe ser un entero positivo.")
            );
        }
        int capacidad = datos.getCapacidad() != null ? datos.getCapacidad() : 4;
        if (capacidad <= 0 || capacidad > 50) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "La capacidad de la mesa debe estar entre 1 y 50 personas.")
            );
        }

        Optional<mesaModel> existenteOpt = mesaRepo.findById(id);
        if (existenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<mesaModel> porNumero = mesaRepo.findByNumero(datos.getNumero());
        if (porNumero.isPresent() && !porNumero.get().getIdMesa().equals(id)) {
            return ResponseEntity.status(409).body(
                Map.of("mensaje", "Ya existe otra mesa con ese número.")
            );
        }

        mesaModel existente = existenteOpt.get();
        existente.setNumero(datos.getNumero());
        existente.setCapacidad(capacidad);
        if (datos.getActiva() != null) {
            existente.setActiva(datos.getActiva());
        }
        return ResponseEntity.ok(mesaRepo.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMesa(@PathVariable Integer id) {
        if (!mesaRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        mesaRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
