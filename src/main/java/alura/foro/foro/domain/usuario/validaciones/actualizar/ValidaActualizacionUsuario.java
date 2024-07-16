package alura.foro.foro.domain.usuario.validaciones.actualizar;

import alura.foro.foro.domain.usuario.ActualizarUsuarioDTO;
import alura.foro.foro.domain.usuario.UsuarioRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidaActualizacionUsuario implements ValidarActualizarUsuario{

    @Autowired
    private UsuarioRepository repository;

    @Override
    public void validate(ActualizarUsuarioDTO data) {
        if(data.email() != null){
            var emailDuplicado = repository.findByEmail(data.email());
            if(emailDuplicado != null){
                throw new ValidationException("El email ingresado, ya se encunetra registrado.");
            }
        }
    }
}

