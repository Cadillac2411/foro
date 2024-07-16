package alura.foro.foro.domain.respuesta.validaciones.actualizar;

import alura.foro.foro.domain.respuesta.ActualizarRespuestaDTO;

public interface ValidarRespuestaActualizada {

    public void validate(ActualizarRespuestaDTO data, Long respuestaId);

}
