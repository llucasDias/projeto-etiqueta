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
 * 2) Gerar conteúdo visual da etiqueta
 * 3) Atualizar status da etiqueta (gerada)
 * 4) Consultar etiquetas
 */
@Service
public class EtiquetaService {

    private final EtiquetaMatrizRepository etiquetaMatrizRepository;

    public EtiquetaService(EtiquetaMatrizRepository etiquetaMatrizRepository) {
        this.etiquetaMatrizRepository = etiquetaMatrizRepository;
    }

    public String conteudoCompactado(EtiquetaMatrizEntity etq) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return String.format(
                "{\"d\":\"%s\",\"o\":\"%s\",\"c\":\"%s\",\"p\":\"%s\",\"i\":\"%s\",\"q\":\"%s\"}",
                etq.getData().format(formatter),
                etq.getOp(),
                etq.getCliente(),
                etq.getPedido(),
                etq.getItem(),
                etq.getQtd()
        );
    }

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

    @Transactional(readOnly = true)
    public EtiquetaMatrizEntity buscar(Integer checkid) {
        return etiquetaMatrizRepository.findByCheckid(checkid)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Etiqueta não encontrada: " + checkid
                ));
    }

    @Transactional
    public boolean atualizarStatus(Integer checkid) {
        EtiquetaMatrizEntity etq = buscar(checkid);
        if (etq.isStatus()) return false;
        etq.setStatus(true);
        etiquetaMatrizRepository.save(etq);
        return true;
    }

    public boolean isEtiquetaGerada(Integer checkid) {
        return buscar(checkid).isStatus();
    }

    public EtiquetaMatrizEntity buscarByCodigo(String codigo) {
        return etiquetaMatrizRepository.findByCodigoEtiqueta(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Etiqueta não encontrada: " + codigo));
    }

    /**
     * Gera arquivo ZPL da etiqueta e marca como gerada
     */
    public void gerarZplEtiqueta(EtiquetaMatrizEntity etq, String caminho) throws Exception {
        if (isEtiquetaGerada(etq.getCheckid())) {
            throw new IllegalStateException("Etiqueta já foi gerada para OP: " + etq.getOp());
        }
        CodigoDeBarra.salvarZplArquivo(etq, caminho);
        atualizarStatus(etq.getCheckid());
    }
}