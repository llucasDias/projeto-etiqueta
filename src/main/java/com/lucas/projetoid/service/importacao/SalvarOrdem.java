package com.lucas.projetoid.service.importacao;


import com.lucas.projetoid.model.EtiquetaErroEntity;
import com.lucas.projetoid.model.EtiquetaMatrizEntity;
import com.lucas.projetoid.repository.EtiquetaErroRepository;
import com.lucas.projetoid.repository.EtiquetaMatrizRepository;
import com.lucas.projetoid.repository.OpProtheusRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Responsável por:
 * 1. Buscar OPs no Protheus
 * 2. Mapear para entidade EtiquetaMatrizEntity
 * 3. Validar se OP já foi importada ou está finalizada
 * 4. Salvar e gerar código de etiqueta sequencial
 **/

@Service
@Transactional
public class SalvarOrdem {

    private final OpProtheusRepository opProtheusRepository;
    private final EtiquetaMatrizRepository etiquetaMatrizRepository;
    private final ValidarItem validarItem;
    private final EtiquetaErroRepository etiquetaErroRepository;

    public SalvarOrdem(OpProtheusRepository opProtheusRepository, EtiquetaMatrizRepository etiquetaMatrizRepository, ValidarItem validarItem, EtiquetaErroRepository etiquetaErroRepository) {
        this.opProtheusRepository = opProtheusRepository;
        this.etiquetaMatrizRepository = etiquetaMatrizRepository;
        this.validarItem = validarItem;
        this.etiquetaErroRepository = etiquetaErroRepository;

    }

    /**
     * Buscar ordens, mapeia, valida duplicidade e gera etiqueta.
     **/

    public List<EtiquetaMatrizEntity> mapearOp() {

        // Última OP importada no banco
        Integer ultimaOp = etiquetaMatrizRepository.findMaxCheckIdAsNumber();

    // Buscar OP no protheus
    List<Map<String, Object>> resultado = opProtheusRepository.buscarOrdens(ultimaOp);

    List<EtiquetaMatrizEntity> ordens = resultado.stream()
            .filter(this::verificarDuplicidadeOuErro)
            .filter(this::verificarPedidoItem)
            .map(this::mapearParaEntidade)
            .map(this::gerarCodigoEtiqueta)
            .collect(Collectors.toList());

    return ordens;

    }


    /**
     * Monta a entidade inicial com os dados da OP
     **/

    private EtiquetaMatrizEntity mapearParaEntidade(Map<String, Object> row) {


        String pedidoVenda = (String) row.get("C2_PEDIDO");
        String itemCodigo = (String) row.get("C2_ITEMPV");


        if (pedidoVenda == null || pedidoVenda.isBlank()) {
            throw new IllegalArgumentException("Pedido de venda está nulo no Protheus.");
        }

        if (itemCodigo == null || itemCodigo.isBlank()) {
            throw new IllegalArgumentException(("Item do pedido de venda está nulo no Protheus"));
        }

        // Formata item como ITEM/totalItens
        String itemFormatado = validarItem.countItens(pedidoVenda, itemCodigo);


        EtiquetaMatrizEntity ordem = new EtiquetaMatrizEntity();

        ordem.setOp((String) row.get("C2_NUM"));
        ordem.setCliente((String) row.get("C5_CLIENT"));
        ordem.setPedido(pedidoVenda);
        ordem.setItem(itemFormatado);
        ordem.setQtd((Double) row.get("C2_QUANT"));
        ordem.setData(((java.sql.Timestamp) row.get("DATA")).toLocalDateTime());
        ordem.setStatus(false);
        ordem.setCheckid(Integer.parseInt(row.get("RECNO_STRING").toString()));


        return ordem;
    }


    /**
     * Gera código de etiqueta sequencial e persiste no banco
     */
    private EtiquetaMatrizEntity gerarCodigoEtiqueta(EtiquetaMatrizEntity etq) {

        etq = etiquetaMatrizRepository.save(etq);

        String codigo = "ET" + String.format("%07d", etq.getId()); // ET0000001

        etq.setCodigoEtiqueta(codigo);

        return  etiquetaMatrizRepository.save(etq);
    }


    /**
     * Verifica duplicidade ou se já possui erro registrado
     */
    public boolean verificarDuplicidadeOuErro(Map<String, Object> row) {

        Integer checkid = Integer.parseInt(row.get("RECNO_STRING").toString());

        if (etiquetaErroRepository.findByCheckid(checkid).isPresent()) {
            System.out.println("OP com erro já registrada. Ignorando CheckID " + checkid);
            return false;
        }

        Optional<EtiquetaMatrizEntity> existente = etiquetaMatrizRepository.findByCheckid(checkid);

        if (existente.isEmpty()) {
            return true;
        }

        EtiquetaMatrizEntity opExistente = existente.get();


        if (opExistente.isStatus()) {
            System.out.println("OP " + opExistente.getOp() +
                    " já existe e está finalizada. Ignorando importação.");
            return false;
        }

        System.out.println("OP " + opExistente.getOp() + " já existe no banco.");
        return false;
    }


    /**
     * Verifica se pedido e item existem e não são nulos
     */
    public boolean verificarPedidoItem(Map<String, Object> row) {

        String pedido = (String) row.get("C2_PEDIDO");
        String item = (String) row.get("C2_ITEMPV");

        if (pedido == null || pedido.isBlank()) {

            registrarErro(row, "Pedido de venda nulo");
            System.out.println("Ignorada - OP " + pedido + " possui pedido de venda nulo.");
            return false;
        }

        if (item == null || item.isBlank()) {

            registrarErro(row, "Item do pedido de venda nulo");
            System.out.println("Ignorada - OP " + pedido + " possui item do pedido nulo.");
            return false;
        }

        return true;
    }


    /**
     * Registra erros no banco para log
     */
    private void registrarErro(Map<String, Object> row, String motivo) {

        Integer checkid = Integer.parseInt(row.get("RECNO_STRING").toString());

        Optional<EtiquetaErroEntity> existente = etiquetaErroRepository.findByCheckid(checkid);
        if (existente.isPresent()) {
            System.out.println("Erro já existente para CheckID " + checkid + ". Ignorando novo registro.");
            return;
        }

        EtiquetaErroEntity erro = new EtiquetaErroEntity();

        erro.setCheckid(checkid);
        erro.setMotivo(motivo);
        erro.setDataErro(LocalDateTime.now());

        etiquetaErroRepository.save(erro);

        System.out.println("OP " + row.get("C2_NUM") + " registrada no log de erros: " + motivo);
    }


}

