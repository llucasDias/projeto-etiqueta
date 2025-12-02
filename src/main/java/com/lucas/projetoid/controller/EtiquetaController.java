package com.lucas.projetoid.controller;

import com.lucas.projetoid.model.EtiquetaMatrizEntity;
import com.lucas.projetoid.repository.EtiquetaMatrizRepository;
import com.lucas.projetoid.service.importacao.SalvarOrdem;
import com.lucas.projetoid.service.etiqueta.EtiquetaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<EtiquetaMatrizEntity> opsPage = etiquetaMatrizRepository.findAll(pageable);

        model.addAttribute("opsPage", opsPage);
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

        redirectAttributes.addAttribute("mensagem", mensagem);
        return "redirect:/";
    }

    // ------------------- TELA GERAR ETIQUETA -------------------
    @GetMapping("/gerar-etiqueta")
    public String telaGerarEtiqueta() {
        return "gerar-etiqueta";
    }

    // ------------------- LISTAR OPs POR PEDIDO -------------------
    @PostMapping("/gerar-etiqueta/listar")
    public String listarPorPedido(@RequestParam("pedido") String pedido, Model model) {
        List<EtiquetaMatrizEntity> lista = etiquetaMatrizRepository.findByPedido(pedido);

        if (lista.isEmpty()) {
            model.addAttribute("erro", "Nenhuma OP encontrada para o pedido: " + pedido);
            return "gerar-etiqueta";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("ops", lista);
        return "gerar-etiqueta-lista";
    }

    // ------------------- GERAR ETIQUETAS ZPL -------------------
    @PostMapping("/gerar-etiqueta/gerar")
    public String gerarEtiquetasPedido(@RequestParam("pedido") String pedido,
                                       RedirectAttributes redirectAttributes) {

        List<EtiquetaMatrizEntity> lista = etiquetaMatrizRepository.findByPedido(pedido);

        if (lista.isEmpty()) {
            redirectAttributes.addAttribute("mensagem", "Nenhuma OP encontrada para gerar etiquetas.");
            return "redirect:/";
        }

        int geradas = 0;
        for (EtiquetaMatrizEntity etq : lista) {
            try {
                String caminho = "C:/etiquetas/" + etq.getCodigoEtiqueta() + ".zpl";
                etiquetaService.gerarZplEtiqueta(etq, caminho);
                geradas++;
            } catch (Exception e) {

                System.out.println(e.getMessage());
            }
        }

        String msg = geradas + " etiqueta(s) gerada(s) em ZPL para o pedido " + pedido;
        redirectAttributes.addAttribute("mensagem", msg);
        return "redirect:/";
    }

    // ------------------- ETIQUETAS JÁ GERADAS (Status = True) -------------------
    @GetMapping("/etiquetas/geradas")
    public String listarEtiquetasGeradas(Model model) {
        List<EtiquetaMatrizEntity> geradas = etiquetaMatrizRepository.findByStatus(true);
        model.addAttribute("opsGeradas", geradas);
        return "etiquetas-geradas"; // HTML que você cria para exibir
    }


}