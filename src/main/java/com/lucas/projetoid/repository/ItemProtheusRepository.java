package com.lucas.projetoid.repository;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/** Repositorio para buscar todos os itens do pedido de venda do Protheus, para validação no formato 1/N **/


@Repository
public class ItemProtheusRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemProtheusRepository (@Qualifier("protheusDataSource") javax.sql.DataSource dataSource)  {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }

    public List<Map<String, Object>> buscarItens(String pedidoVenda) {

        String sql = """
            SELECT
            SC2.C2_ITEMPV
        FROM SC2010 as SC2
        WHERE SC2.C2_PEDIDO = ? AND
        SC2.D_E_L_E_T_ = ''
        """;

        return jdbcTemplate.queryForList(sql, pedidoVenda);

    }

}
