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

    public static String gerarZebraZpl(EtiquetaMatrizEntity etq) {

        int width  = 812; // etiqueta 104mm  → ZEBRA REAL
        int height = 406; // etiqueta 50,8mm → OK Labelary

        String codigo = String.format("%s-%s-%s-%s-%s",
                etq.getOp(), etq.getPedido(), etq.getItem(),
                etq.getCliente(), etq.getQtd()
        );

        String linha1 = "CLIENTE: " + etq.getCliente();
        String linha2 = "OP: " + etq.getOp() + "   ITEM: " + etq.getItem();
        String linha3 = "PEDIDO: " + etq.getPedido();
        String linha4 = "DATA: " + etq.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"))
                + "QTD:" + etq.getQtd();

        StringBuilder zpl = new StringBuilder();

        zpl.append("^XA");
        zpl.append("^PW812");
        zpl.append("^LL406");

        // ===== TEXTO CENTRALIZADO =====
        zpl.append("^FO0,20^FB812,1,0,C,0^A0N,32,32^FD" + linha1 + "\\&^FS");
        zpl.append("^FO0,60^FB812,1,0,C,0^A0N,32,32^FD" + linha2 + "\\&^FS");
        zpl.append("^FO0,100^FB812,1,0,C,0^A0N,32,32^FD" + linha3 + "\\&^FS");
        zpl.append("^FO0,140^FB812,1,0,C,0^A0N,32,32^FD" + linha4 + "\\&^FS");


        zpl.append("^FO0,210");
        zpl.append("^BY2,3,120");
        zpl.append("^BCN,120,Y,N,N");
        zpl.append("^FD" + codigo + "^FS");
        zpl.append("^XZ");
        return zpl.toString();
    }

    public static void salvarZplArquivo(EtiquetaMatrizEntity etq, String caminho) throws IOException {
        String zpl = gerarZebraZpl(etq);
        Files.writeString(Path.of(caminho), zpl);
        System.out.println("ZPL gerado em: " + caminho);
    }
}
