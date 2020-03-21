package br.com.github.reativo.controller;

import br.com.github.reativo.model.Agenda;
import br.com.github.reativo.repository.AgendaRepository;
import java.net.URI;
import java.time.Duration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("agendas")
@RestController
public class AgendaController {
    
    private final AgendaRepository repository;

    public AgendaController(AgendaRepository repository) {
        this.repository = repository;
    }
    
    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Agenda> todos() {
        
        return this.repository.findAll().delayElements(Duration.ofMillis(200)).log();
    }
    
    @GetMapping("{id}")
    public Mono<ResponseEntity<Agenda>> buscarPor(@PathVariable Integer id) {
        
        return this.repository.findById(id)
                .map(agenda -> ResponseEntity.ok(agenda))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Mono<ResponseEntity<Agenda>> salvar(@RequestBody Mono<Agenda> agenda) {
        
        return agenda.flatMap(a -> this.repository.save(a))
                .map(a -> ResponseEntity.created(URI.create("/agendas/"+a.getId())).body(a));
    }
    
    @PutMapping("{id}")
    public Mono<ResponseEntity<Agenda>> alterar(@PathVariable Integer id, @RequestBody Mono<Agenda> agenda) {
        
        return agenda.filter(a -> a.getId().equals(id))
                .flatMap(a -> this.repository.findById(a.getId())
                        .flatMap(editar -> {
                            editar.setNome(a.getNome());
                            editar.setEmail(a.getEmail());
                            return this.repository.save(editar);
                        })
                
                ).map(a -> ResponseEntity.ok(a))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> excluir(@PathVariable Integer id) {
        
        return this.repository.findById(id)
                .flatMap(a -> this.repository.deleteById(a.getId()).thenReturn(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
