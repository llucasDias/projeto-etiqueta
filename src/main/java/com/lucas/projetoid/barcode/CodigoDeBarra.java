package com.lucas.projetoid.barcode;

import org.springframework.stereotype.Component;
import com.lucas.projetoid.model.EtiquetaMatrizEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Classe utilitária para gerar etiquetas Zebra (ZPL) a partir de uma EtiquetaMatrizEntity.
 * Todos os dados da etiqueta (op, cliente, pedido, item, qtd, data) são usados.
 * Salva o ZPL em arquivo ou retorna a string ZPL.
 */
@Component
public class CodigoDeBarra {

    /**
     * Gera o ZPL da etiqueta.
     *
     * @param etq EtiquetaMatrizEntity
     * @return String contendo ZPL
     */
    public static String gerarZebraZpl(EtiquetaMatrizEntity etq) {

        int x = 10;          // posição X inicial
        int y = 10;          // posição Y inicial
        int qrSize = 200;    // tamanho do QRCode

        // Conteúdo compacto para QRCode (JSON)
        String conteudo = String.format(
                "{\"d\":\"%s\",\"o\":\"%s\",\"c\":\"%s\",\"p\":\"%s\",\"i\":\"%s\",\"q\":\"%s\"}",
                etq.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy")),
                etq.getOp(),
                etq.getCliente(),
                etq.getPedido(),
                etq.getItem(),
                etq.getQtd()
        );

        // Texto visual abaixo do QR
        String textoVisual = String.format(
                "CLIENTE: %s%nOP: %s   ITEM: %s%nPEDIDO: %s%nDATA: %s   QTD: %s",
                etq.getCliente(),
                etq.getOp(),
                etq.getItem(),
                etq.getPedido(),
                etq.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy")),
                etq.getQtd()
        );

        // Monta ZPL
        StringBuilder zpl = new StringBuilder();
        zpl.append("^XA\n"); // Início etiqueta

        // QRCode
        zpl.append(String.format("^FO%d,%d^BQN,2,5^FDLA,%s^FS\n", x, y, conteudo));

        // Texto
        int yText = y + qrSize + 10;
        for (String linha : textoVisual.split("\n")) {
            zpl.append(String.format("^FO%d,%d^A0N,30,30^FD%s^FS\n", x, yText, linha));
            yText += 35;
        }

        zpl.append("^XZ"); // Fim etiqueta

        return zpl.toString();
    }

    /**
     * Salva a etiqueta em arquivo ZPL
     *
     * @param etq     EtiquetaMatrizEntity
     * @param caminho Caminho do arquivo (.zpl)
     * @throws IOException
     */
    public static void salvarZplArquivo(EtiquetaMatrizEntity etq, String caminho) throws IOException {
        String zpl = gerarZebraZpl(etq);
        Files.writeString(Path.of(caminho), zpl);
        System.out.println("ZPL gerado em: " + caminho);
    }
}