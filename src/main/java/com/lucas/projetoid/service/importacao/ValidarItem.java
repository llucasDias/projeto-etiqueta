package com.lucas.projetoid.service.importacao;


import com.lucas.projetoid.repository.ItemProtheusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ValidarItem {

    private final ItemProtheusRepository itemProtheusRepository;

    public ValidarItem(ItemProtheusRepository itemProtheusRepository) {
        this.itemProtheusRepository = itemProtheusRepository;
    }

    public String countItens(String pedidoVenda, String itemCodigo) {

        List<Map<String, Object>> itensDoPedido = itemProtheusRepository.buscarItens(pedidoVenda);

        List<String> itensOrdenados = itensDoPedido.stream()
                .map(row->(String) row.get("C2_ITEMPV"))
                .sorted()
                .toList();

        int totalItens = itensOrdenados.size();


        return itemCodigo + "/" + totalItens;

    }

  }
