package alura.foro.foro.controller;


import alura.foro.foro.domain.curso.Curso;
import alura.foro.foro.domain.curso.CursoRepository;
import alura.foro.foro.domain.respuesta.DetalleRespuestaDTO;
import alura.foro.foro.domain.respuesta.Respuesta;
import alura.foro.foro.domain.respuesta.RespuestaRepository;
import alura.foro.foro.domain.topico.*;
import alura.foro.foro.domain.topico.validaciones.actualizar.ValidarTopicoActualizado;
import alura.foro.foro.domain.topico.validaciones.crear.ValidarTopicoCreado;
import alura.foro.foro.domain.usuario.Usuario;
import alura.foro.foro.domain.usuario.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Topic", description = "Se vincula con un curso y usuario.")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    CursoRepository cursoRepository;

    @Autowired
    RespuestaRepository respuestaRepository;

    @Autowired
    List<ValidarTopicoCreado> crearValidadores;

    @Autowired
    List<ValidarTopicoActualizado> actualizarValidadores;

    @PostMapping
    @Transactional
    @Operation(summary = "Registra un nuevo topico en DB")
    public ResponseEntity<DetallesTopicoDTO> crearTopico(@RequestBody @Valid CrearTopicoDTO crearTopicoDTO, UriComponentsBuilder uriBuilder){
        crearValidadores.forEach(v -> v.validate(crearTopicoDTO));

        Usuario usuario = usuarioRepository.findById(crearTopicoDTO.usuarioId()).get();
        Curso curso = cursoRepository.findById(crearTopicoDTO.cursoId()).get();
        Topico topico = new Topico(crearTopicoDTO, usuario, curso);

        topicoRepository.save(topico);

        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetallesTopicoDTO(topico));
    }

    @GetMapping
    @Operation(summary = "Muestra de temas abiertos y cerrados")
    public ResponseEntity<Page<DetallesTopicoDTO>> leerTopicosNoEliminados(@PageableDefault(size = 5, sort = {"ultimaActualizacion"}, direction = Sort.Direction.DESC) Pageable pageable){
        var pagina = topicoRepository.findAllByEstadoIsNot(Estado.DELETED, pageable).map(DetallesTopicoDTO::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/todos")
    @Operation(summary = "Muestra todos los temas independientemente ")
    public ResponseEntity<Page<DetallesTopicoDTO>> leerTodosTopicos(@PageableDefault(size = 5, sort = {"ultimaActualizacion"}, direction = Sort.Direction.DESC)Pageable pageable){
        var pagina = topicoRepository.findAll(pageable).map(DetallesTopicoDTO::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    @Operation(summary = "muestra el tema consultado por medio de ID")
    public ResponseEntity<DetallesTopicoDTO> leerUnTopico(@PathVariable Long id){
        Topico topico = topicoRepository.getReferenceById(id);
        var datosTopico = new DetallesTopicoDTO(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getUltimaActualizacion(),
                topico.getEstado(),
                topico.getUsuario().getUsername(),
                topico.getCurso().getName(),
                topico.getCurso().getCategoria()
        );
        return ResponseEntity.ok(datosTopico);
    }

    @GetMapping("/{id}/solucion")
    @Operation(summary = "Muestra respuesta que se considera solución")
    public ResponseEntity<DetalleRespuestaDTO> leerSolucionTopico(@PathVariable Long id){
        Respuesta respuesta = respuestaRepository.getReferenceByTopicoId(id);

        var datosRespuesta = new DetalleRespuestaDTO(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getFechaCreacion(),
                respuesta.getUltimaActualizacion(),
                respuesta.getSolucion(),
                respuesta.getBorrado(),
                respuesta.getUsuario().getId(),
                respuesta.getUsuario().getUsername(),
                respuesta.getTopico().getId(),
                respuesta.getTopico().getTitulo()
        );
        return ResponseEntity.ok(datosRespuesta);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Actualiza el título, estado, mensaje mensaje o ID del curso de un tema")
    public ResponseEntity<DetallesTopicoDTO> actualizarTopico(@RequestBody @Valid ActualizarTopicoDTO actualizarTopicoDTO, @PathVariable Long id){
        actualizarValidadores.forEach(v -> v.validate(actualizarTopicoDTO));

        Topico topico = topicoRepository.getReferenceById(id);

        if(actualizarTopicoDTO.cursoId() != null){
            Curso curso = cursoRepository.getReferenceById(actualizarTopicoDTO.cursoId());
            topico.actualizarTopicoConCurso(actualizarTopicoDTO, curso);
        }else{
            topico.actualizarTopico(actualizarTopicoDTO);
        }

        var datosTopico = new DetallesTopicoDTO(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getUltimaActualizacion(),
                topico.getEstado(),
                topico.getUsuario().getUsername(),
                topico.getCurso().getName(),
                topico.getCurso().getCategoria()
        );
        return ResponseEntity.ok(datosTopico);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Elimina un tema")
    public ResponseEntity<?> eliminarTopico(@PathVariable Long id){
        Topico topico = topicoRepository.getReferenceById(id);
        topico.eliminarTopico();
        return ResponseEntity.noContent().build();
    }

}






