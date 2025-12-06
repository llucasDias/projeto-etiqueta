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

        int width  = 812; // largura da etiqueta
        int height = 406; // altura da etiqueta

        // Dados embutidos no QR Code com TAB (Excel separa em colunas)
        String codigo = String.join("\t",
                etq.getOp(),
                etq.getPedido(),
                etq.getItem(),
                etq.getCliente(),
                etq.getQtd().toString()
        );

        // Texto da etiqueta
        String linha1 = "CLIENTE: " + etq.getCliente();
        String linha2 = "OP: " + etq.getOp() + "   ITEM: " + etq.getItem();
        String linha3 = "PEDIDO: " + etq.getPedido();
        String linha4 = "DATA: " + etq.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"))
                + " QTD:" + etq.getQtd();

        StringBuilder zpl = new StringBuilder();

        zpl.append("^XA");
        zpl.append("^PW812");
        zpl.append("^LL406");


        zpl.append("^FO30,123^A0N,32,32^FD" + linha1 + "^FS");
        zpl.append("^FO30,163^A0N,32,32^FD" + linha2 + "^FS");
        zpl.append("^FO30,203^A0N,32,32^FD" + linha3 + "^FS");
        zpl.append("^FO30,243^A0N,32,32^FD" + linha4 + "^FS");


        zpl.append("^FO500,110");          // Centralizado verticalmente em relação ao bloco de texto
        zpl.append("^BQN,2,8");
        zpl.append("^FDLA," + codigo + "^FS");

        zpl.append("^XZ");
        return zpl.toString();
    }

    public static void salvarZplArquivo(EtiquetaMatrizEntity etq, String caminho) throws IOException {
        String zpl = gerarZebraZpl(etq);
        Files.writeString(Path.of(caminho), zpl);
        System.out.println("ZPL gerado em: " + caminho);
    }
}
