package com.lucas.projetoid.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repositório responsável pela comunicação direta com o banco Protheus.
 *
 * Essa classe utiliza o JdbcTemplate para executar consultas SQL nativas,
 **/



@Repository
public class OrdemProducaoRepository {

    /**
     * Injeta o JdbcTemplate configurado na aplicação.
     * O JdbcTemplate gerencia conexões, execução de queries e tratamento de exceções.
     * @param jdbcTemplate componente Spring para execução de SQL nativo
     */


    private final JdbcTemplate jdbcTemplate;

    public OrdemProducaoRepository(@Qualifier("protheusDataSource") javax.sql.DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> buscarOrdens() {
        String sql = """
            SELECT
            SC2.C2_NUM,
            SC2.C2_PEDIDO,
            SC2.C2_ITEMPV,
            SC2.C2_QUANT,
            SC5.C5_CLIENT,
            GETDATE() as DATA
        FROM SC2010 as SC2
        INNER JOIN SC5010 SC5 ON SC2.C2_PEDIDO = SC5.C5_NUM
        WHERE SC2.C2_PEDIDO = '001242'
        """;

        return jdbcTemplate.queryForList(sql);

    }

}
