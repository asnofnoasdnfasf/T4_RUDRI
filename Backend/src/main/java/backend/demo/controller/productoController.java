package backend.demo.controller;

import backend.demo.model.productoModel;
import backend.demo.repository.productoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class productoController {

    private final productoRepository productoRepo;

    public productoController(productoRepository productoRepo) {
        this.productoRepo = productoRepo;
    }

    @GetMapping
    public List<productoModel> listarProductos() {
        return productoRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody(required = false) productoModel nuevo) {
        if (nuevo == null || nuevo.getNombre() == null || nuevo.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "El nombre del producto es obligatorio.")
            );
        }
        if (nuevo.getPrecio() == null || nuevo.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "El precio del producto debe ser mayor a cero.")
            );
        }

        nuevo.setNombre(nuevo.getNombre().trim().toUpperCase());
        nuevo.setPrecio(nuevo.getPrecio().setScale(2, RoundingMode.HALF_UP));
        if (nuevo.getDisponible() == null) {
            nuevo.setDisponible(true);
        }

        return ResponseEntity.ok(productoRepo.save(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Integer id,
            @RequestBody(required = false) productoModel datos) {
        if (datos == null || datos.getNombre() == null || datos.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "El nombre del producto es obligatorio.")
            );
        }
        if (datos.getPrecio() == null || datos.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "El precio del producto debe ser mayor a cero.")
            );
        }

        Optional<productoModel> existenteOpt = productoRepo.findById(id);
        if (existenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productoModel existente = existenteOpt.get();
        existente.setNombre(datos.getNombre().trim().toUpperCase());
        existente.setPrecio(datos.getPrecio().setScale(2, RoundingMode.HALF_UP));
        if (datos.getDisponible() != null) {
            existente.setDisponible(datos.getDisponible());
        }
        if (datos.getCategoria() != null) {
            existente.setCategoria(datos.getCategoria());
        }
        return ResponseEntity.ok(productoRepo.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        if (!productoRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
