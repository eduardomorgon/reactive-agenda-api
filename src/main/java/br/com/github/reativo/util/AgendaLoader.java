package br.com.github.reativo.util;

import br.com.github.reativo.model.Agenda;
import br.com.github.reativo.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class AgendaLoader implements CommandLineRunner {
    
    @Autowired
    private AgendaRepository repository;

    @Override
    public void run(String... args) throws Exception {

        Flux<Agenda> agendas = Flux.range(1, 5)
                .map(cont -> {
                    String nome = "email"+cont;
                    Agenda agenda = new Agenda();
                    agenda.setNome(nome);
                    agenda.setEmail(nome+"@email.com");
                    return agenda;
                });
        repository.saveAll(agendas).log().subscribe();
    }
    
}
