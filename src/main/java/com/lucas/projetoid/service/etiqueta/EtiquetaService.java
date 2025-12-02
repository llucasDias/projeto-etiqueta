package com.lucas.projetoid.service.etiqueta;

import com.lucas.projetoid.barcode.CodigoDeBarra;
import com.lucas.projetoid.model.EtiquetaMatrizEntity;
import com.lucas.projetoid.repository.EtiquetaMatrizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;


/**
 * Service responsável por:
 * 1) Gerar conteúdo compacto (JSON) para ZPL
 * 2) Gerar conteúdo visual (texto) da etiqueta
 * 3) Atualizar status da etiqueta (gerada)
 * 4) Consultar etiquetas
 */
@Service
public class EtiquetaService {

    private final EtiquetaMatrizRepository etiquetaMatrizRepository;

    public EtiquetaService(EtiquetaMatrizRepository etiquetaMatrizRepository) {
        this.etiquetaMatrizRepository = etiquetaMatrizRepository;
    }

    /**
     * Monta conteúdo compacto da etiqueta (JSON) incluindo quantidade
     */
    public String conteudoCompactado(EtiquetaMatrizEntity etq) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String dataFormatada = etq.getData().format(formatter);

        return String.format(
                "{\"d\":\"%s\",\"o\":\"%s\",\"c\":\"%s\",\"p\":\"%s\",\"i\":\"%s\",\"q\":\"%s\"}",
                dataFormatada,
                etq.getOp(),
                etq.getCliente(),
                etq.getPedido(),
                etq.getItem(),
                etq.getQtd()
        );
    }

    /**
     * Conteúdo visual da etiqueta (texto abaixo do QR/ZPL)
     */
    public String textoVisual(EtiquetaMatrizEntity etq) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yy");
        return String.format(
                "CLIENTE: %s%nOP: %s   ITEM: %s%nPEDIDO: %s%nDATA: %s   QTD: %s",
                etq.getCliente(),
                etq.getOp(),
                etq.getItem(),
                etq.getPedido(),
                etq.getData().format(fmt),
                etq.getQtd()
        );
    }

    /**
     * Busca etiqueta pelo checkid
     */
    @Transactional(readOnly = true)
    public EtiquetaMatrizEntity buscar(Integer checkid) {
        return etiquetaMatrizRepository
                .findByCheckid(checkid)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Etiqueta não encontrada: " + checkid
                ));
    }

    /**
     * Atualiza status da etiqueta (true = gerada)
     */
    @Transactional
    public boolean atualizarStatus(Integer checkid) {
        EtiquetaMatrizEntity etq = buscar(checkid);
        if (etq.isStatus()) return false;
        etq.setStatus(true);
        etiquetaMatrizRepository.save(etq);
        return true;
    }

    /**
     * Verifica se a etiqueta já foi gerada
     */
    public boolean isEtiquetaGerada(Integer checkid) {
        EtiquetaMatrizEntity etq = buscar(checkid);
        return etq.isStatus();
    }

    /**
     * Busca por código da etiqueta
     */
    public EtiquetaMatrizEntity buscarByCodigo(String codigo) {
        return etiquetaMatrizRepository
                .findByCodigoEtiqueta(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Etiqueta não encontrada: " + codigo));
    }

    /**
     * Gera arquivo ZPL da etiqueta e atualiza status
     */
    public void gerarZplEtiqueta(EtiquetaMatrizEntity etq, String caminho) throws Exception {
        // Verifica se já foi gerada
        if (isEtiquetaGerada(etq.getCheckid())) {
            throw new IllegalStateException("Etiqueta já foi gerada para OP: " + etq.getOp());
        }

        // Salva ZPL
        CodigoDeBarra.salvarZplArquivo(etq, caminho);

        // Marca como gerada
        atualizarStatus(etq.getCheckid());
    }
}