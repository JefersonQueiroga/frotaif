package br.edu.ifrn.frotaif.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Parâmetros de viagem lidos do application.yml (prefixo "frota.viagem").
 * Ex.: frota.viagem.antecedencia-minima-horas → antecedenciaMinimaHoras
 */
@ConfigurationProperties(prefix = "frota.viagem")
public record ViagemProperties(
        @DefaultValue("48") int antecedenciaMinimaHoras // RN04
) {
}
