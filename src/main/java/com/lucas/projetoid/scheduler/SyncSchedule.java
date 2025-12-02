package com.lucas.projetoid.scheduler;

import com.lucas.projetoid.service.importacao.SalvarOrdem;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/*

@Component
public class SyncSchedule {

    private final SalvarOrdem salvarOrdem;

    public SyncSchedule(SalvarOrdem salvarOrdem) {
        this.salvarOrdem = salvarOrdem;
    }

    @Scheduled(fixedDelay = 60000)
    public void execSync () {

        try {
            int ordensImportadas = salvarOrdem.mapearOp().size();
            System.out.println("Ordens Importadas" + ordensImportadas);
        } catch (Exception e) {
            System.err.println("Erro durante a importação: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
*/