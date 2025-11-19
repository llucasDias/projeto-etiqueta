package com.lucas.projetoid.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ClienteProtheusRepository {


    /**
     * Injeta o JdbcTemplate configurado na aplicação.
     * O JdbcTemplate gerencia conexões, execução de queries e tratamento de exceções.
     * @param jdbcTemplate componente Spring para execução de SQL nativo
     */


    private final JdbcTemplate jdbcTemplate;

    public ClienteProtheusRepository(@Qualifier("protheusDataSource") javax.sql.DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public String buscarCliente(String codigo) {
        String sql = """
                    SELECT
                    SA1.A1_NOME
                    FROM SA1010 as SA1
                    WHERE SA1.A1_COD = ?
                """;


        return jdbcTemplate.queryForObject(sql, String.class, codigo);
    }

    }
