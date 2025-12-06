package com.lucas.projetoid.controller;

import com.lucas.projetoid.barcode.CodigoDeBarra;
import com.lucas.projetoid.model.EtiquetaMatrizEntity;
import com.lucas.projetoid.repository.EtiquetaMatrizRepository;
import com.lucas.projetoid.service.importacao.SalvarOrdem;
import com.lucas.projetoid.service.etiqueta.EtiquetaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class EtiquetaController {

    private final EtiquetaMatrizRepository etiquetaMatrizRepository;
    private final SalvarOrdem salvarOrdem;
    private final EtiquetaService etiquetaService;

    public EtiquetaController(
            EtiquetaMatrizRepository etiquetaMatrizRepository,
            SalvarOrdem salvarOrdem,
            EtiquetaService etiquetaService
    ) {
        this.etiquetaMatrizRepository = etiquetaMatrizRepository;
        this.salvarOrdem = salvarOrdem;
        this.etiquetaService = etiquetaService;
    }

    // ------------------- HOME -------------------
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String mensagem) {

        LocalDateTime segundaFeira = LocalDateTime.of(2025, 12, 8, 0, 0);
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());

        Page<EtiquetaMatrizEntity> opsPage =
                etiquetaMatrizRepository.findByDataAfter(segundaFeira, pageable);

        model.addAttribute("ops", opsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", opsPage.getTotalPages());

        if (mensagem != null && !mensagem.isBlank()) {
            model.addAttribute("mensagem", mensagem);
        }

        return "index";
    }

    // ------------------- IMPORTAR OPs -------------------
    @GetMapping("/importar")
    public String importarOps(RedirectAttributes redirectAttributes) {
        List<EtiquetaMatrizEntity> importadas = salvarOrdem.mapearOp();

        String mensagem = importadas.isEmpty()
                ? "Nenhuma OP nova para importar."
                : importadas.size() + " OP(s) importada(s) com sucesso!";

        redirectAttributes.addFlashAttribute("mensagem", mensagem);
        return "redirect:/";
    }

    // ------------------- TELA GERAR ETIQUETA -------------------
    @GetMapping("/gerar-etiqueta")
    public String telaGerarEtiqueta() {
        return "gerar-etiqueta";
    }

    // ------------------- LISTAR OPs POR PEDIDO -------------------
    @PostMapping("/gerar-etiqueta/listar")
    public String listarPorPedido(@RequestParam("pedido") String pedido, Model model, RedirectAttributes redirectAttributes) {
        List<EtiquetaMatrizEntity> lista = etiquetaMatrizRepository.findByPedido(pedido);

        if (lista.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Nenhuma OP encontrada para o pedido: " + pedido);
            return "redirect:/gerar-etiqueta";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("ops", lista);
        return "gerar-etiqueta-lista";
    }

    // ------------------- GERAR ETIQUETAS ZPL -------------------
    @PostMapping("/gerar-etiqueta/gerar")
    public ResponseEntity<byte[]> gerarEtiquetasPedido(@RequestParam(value = "ids", required = false) List<Integer> ids) throws Exception {

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<EtiquetaMatrizEntity> lista = etiquetaMatrizRepository.findAllById(ids);

        // SE SÓ TIVER 1 ETIQUETA -> RETORNA .ZPL DIRETO (usa CodigoDeBarra)
        if (lista.size() == 1) {
            EtiquetaMatrizEntity etq = lista.get(0);

            String zpl = CodigoDeBarra.gerarZebraZpl(etq);

            etiquetaService.atualizarStatus(etq.getCheckid());

            byte[] zplBytes = zpl.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + etq.getCodigoEtiqueta() + ".zpl");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zplBytes.length)
                    .body(zplBytes);
        }


        byte[] zipFile = etiquetaService.gerarZipEtiquetas(lista);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etiquetas.zip");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length)
                .body(zipFile);
    }

    // ------------------- ETIQUETAS JÁ GERADAS (Status = True) -------------------
    @GetMapping("/etiquetas/geradas")
    public String listarEtiquetasGeradas(Model model,
                                         @RequestParam(defaultValue = "0") int page) {

        LocalDateTime segundaFeira = LocalDateTime.of(2025, 12, 8, 0, 0);;

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<EtiquetaMatrizEntity> geradas =
                etiquetaMatrizRepository.findByStatusAndDataAfter(true, segundaFeira, pageable);

        model.addAttribute("opsGeradasPage", geradas);
        return "etiquetas-geradas";
    }

}