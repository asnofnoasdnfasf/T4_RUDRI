package backend.demo.controller;

import backend.demo.repository.usuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class usuarioController {

    private final usuarioRepository usuarioRepo;

    public usuarioController(usuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping
    public List<Map<String, Object>> listarUsuarios() {
        return usuarioRepo.findAll().stream()
            .map(u -> Map.<String, Object>of(
                "idUsuario", u.getIdUsuario() != null ? u.getIdUsuario() : 0,
                "nombreCompleto", u.getNombreCompleto() != null ? u.getNombreCompleto() : "",
                "nombreUsuario", u.getNombreUsuario() != null ? u.getNombreUsuario() : "",
                "activo", Boolean.TRUE.equals(u.getActivo())
            ))
            .toList();
    }
}
