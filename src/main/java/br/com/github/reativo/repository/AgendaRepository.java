package br.com.github.reativo.repository;

import br.com.github.reativo.model.Agenda;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaRepository extends ReactiveCrudRepository<Agenda, Integer>{
    
}
