package com.lucas.projetoid.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repositório responsável pela comunicação direta com o banco Protheus.
 * Essa classe utiliza o JdbcTemplate para executar consultas SQL nativas,
 **/



@Repository
public class OpProtheusRepository {

    /**
     * Injeta o JdbcTemplate configurado na aplicação.
     * O JdbcTemplate gerencia conexões, execução de queries e tratamento de exceções.
     * JdbcTemplate componente Spring para execução de SQL nativo
     **/


    private final JdbcTemplate jdbcTemplate;

    public OpProtheusRepository(@Qualifier("protheusDataSource") javax.sql.DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> buscarOrdens(Integer ultimaOp) {

        String sql = """
            SELECT TOP 10
            SC2.C2_NUM,
            SC2.C2_PEDIDO,
            SC2.C2_ITEMPV,
            SC2.C2_QUANT,
            SC5.C5_CLIENT,
            GETDATE() as DATA,
            CAST(SC2.R_E_C_N_O_ AS VARCHAR) AS RECNO_STRING
        
        FROM SC2010 as SC2
        INNER JOIN SC5010 SC5 ON SC2.C2_PEDIDO = SC5.C5_NUM
        WHERE SC2.D_E_L_E_T_ = '' AND SC5.D_E_L_E_T_ = ''
        AND SC2.R_E_C_N_O_ > CAST(? AS BIGINT)
        AND SC2.C2_DATPRI >= '20251101'
        ORDER BY SC2.R_E_C_N_O_ ASC
        """;

        return jdbcTemplate.queryForList(sql, ultimaOp);

    }

}
