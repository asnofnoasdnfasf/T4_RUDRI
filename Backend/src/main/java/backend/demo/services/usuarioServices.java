package backend.demo.services;

import backend.demo.model.usuarioModel;
import backend.demo.repository.usuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class usuarioServices {
    private static final Pattern PATRON_SQLI = Pattern.compile(
            "('|--|;|/\\*|\\*/|\\bOR\\b\\s+['\"]?\\d+['\"]?\\s*=\\s*['\"]?\\d+|\\bUNION\\b|\\bSELECT\\b|\\bDROP\\b)",
            Pattern.CASE_INSENSITIVE
    );

    private final usuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public usuarioServices(
            usuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean contienePatronSQLi(String texto) {
        if (texto == null || texto.isBlank()) {
            return false;
        }
        return PATRON_SQLI.matcher(texto.trim()).find();
    }

    public Optional<usuarioModel> autenticar(
            String nombreUsuario,
            String clave) {

        if (nombreUsuario == null || nombreUsuario.isBlank()
                || clave == null || clave.isBlank()) {
            return Optional.empty();
        }

        String usuarioNormalizado = nombreUsuario.trim();
        String claveNormalizada = clave.trim();

        if (contienePatronSQLi(usuarioNormalizado) || contienePatronSQLi(claveNormalizada)) {
            return Optional.empty();
        }

        Optional<usuarioModel> usuario =
                usuarioRepository.findByNombreUsuarioAndActivoTrue(usuarioNormalizado);

        return usuario.filter(
                u -> u.getClaveHash() != null && passwordEncoder.matches(
                        claveNormalizada,
                        u.getClaveHash()
                )
        );
    }
}
