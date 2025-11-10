package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Pedido;
import com.example.puntofacil.demo.repository.PedidoRepository;

@Controller
@RequestMapping("/empleado/panel")
public class EmpleadoPanelController {

    private final PedidoRepository pedidoRepository;

    public EmpleadoPanelController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // MÉTODOS PARA PANEL ADMIN
    @GetMapping("/admin")
    public String panelAdmin() {
        System.out.println("✅ DEBUG: Accediendo a panel admin");
        return "panel-admin"; // existe panel-admin.html en templates
    }

    @GetMapping("/cajera")
    public String panelCajera() {
        System.out.println("✅ DEBUG: Accediendo a panel cajera");
        return "panel-cajera"; // panel-cajera.html
    }

    @GetMapping("/vendedor")
    public String panelVendedor() {
        System.out.println("✅ DEBUG: Accediendo a panel vendedor");
        return "panel-vendedor"; // panel-vendedor.html
    }

    // NUEVOS MÉTODOS PARA ENLACES
    @GetMapping("/ventas")
    public String panelVentas(Model model) {
        System.out.println("✅ Accediendo a ventas");
        return "empleado-ventas"; // empleado-ventas.html
    }

    // STOCK AHORA USA EMPLEADO-PRODUCTOS
    @GetMapping("/stock")
    public String panelStock(Model model) {
        System.out.println("✅ Accediendo a stock");
        return "empleado-productos"; // empleado-productos.html
    }

    @GetMapping("/inicio")
    public String panelInicio() {
        System.out.println("✅ Accediendo a inicio");
        return "redirect:/empleado/panel/admin";
    }

    @GetMapping
    public String panelHome(Model model) {
        // Pedidos por estado
        List<Pedido> todos = pedidoRepository.findAll();
        Map<String, Long> cuentasPorEstado = todos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado() == null ? "SIN_ESTADO" : p.getEstado(),
                        Collectors.counting()
                ));
        model.addAttribute("cuentasPorEstado", cuentasPorEstado);

        // Ventas mes actual y año actual
        YearMonth ym = YearMonth.now();
        LocalDate desdeMes = ym.atDay(1);
        LocalDate hastaMes = ym.atEndOfMonth();

        LocalDate desdeAnio = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate hastaAnio = LocalDate.of(LocalDate.now().getYear(), 12, 31);

        BigDecimal totalMes = pedidoRepository.sumTotalByFechaBetween(desdeMes, hastaMes);
        BigDecimal totalAnio = pedidoRepository.sumTotalByFechaBetween(desdeAnio, hastaAnio);

        model.addAttribute("totalMes", totalMes == null ? BigDecimal.ZERO : totalMes);
        model.addAttribute("totalAnio", totalAnio == null ? BigDecimal.ZERO : totalAnio);

        // lista de pedidos recientes
        model.addAttribute("ultimosPedidos", 
                pedidoRepository.findByFechaBetween(desdeMes.minusDays(7), hastaMes));

        // OJO: esta vista sí deberías tenerla como empleado/panel-home.html,
        // si no la usás todavía, no pasa nada mientras no entres a /empleado/panel
        return "empleado/panel-home";
    }

    @GetMapping("/pedidos")
    public String listarPedidos(@RequestParam(value = "estado", required = false) String estado,
                                Model model) {
        List<Pedido> pedidos;
        if (estado != null && !estado.isBlank()) {
            pedidos = pedidoRepository.findByEstado(estado);
        } else {
            pedidos = pedidoRepository.findAll();
        }
        model.addAttribute("pedidos", pedidos);

        // 🔴 CORREGIDO: el template se llama pedidos-list.html (sin carpeta empleado)
        return "pedidos-list";
    }

    @GetMapping("/pedido/{id}")
    public String detallePedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) return "redirect:/empleado/panel?errNotFound";

        model.addAttribute("pedido", pedido);

        // 🔴 CORREGIDO: el template se llama pedido-detalle.html (sin carpeta empleado)
        return "pedido-detalle";
    }

    // Endpoint sencillo para cambiar estado desde panel (ejecutar desde un form pequeño)
    @PostMapping("/pedido/{id}/estado")
    public String actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        pedidoRepository.findById(id).ifPresent(p -> {
            p.setEstado(estado);
            pedidoRepository.save(p);
        });
        return "redirect:/empleado/pedido/" + id;
    }
}
