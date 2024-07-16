package alura.foro.foro.domain.respuesta.validaciones.actualizar;

import alura.foro.foro.domain.respuesta.ActualizarRespuestaDTO;
import alura.foro.foro.domain.respuesta.Respuesta;
import alura.foro.foro.domain.respuesta.RespuestaRepository;
import alura.foro.foro.domain.topico.Estado;
import alura.foro.foro.domain.topico.TopicoRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SolucionDuplicada implements ValidarRespuestaActualizada{

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Override
    public void validate(ActualizarRespuestaDTO data, Long respuestaId) {
       if (data.solucion()){
           Respuesta respuesta = respuestaRepository.getReferenceById(respuestaId);
           var topicoResuelto = topicoRepository.getReferenceById(respuesta.getTopico().getId());
           if (topicoResuelto.getEstado() == Estado.CLOSED){
               throw new ValidationException("El topico ha sido solucionado.");
           }
       }
    }
}


