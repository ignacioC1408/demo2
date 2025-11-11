package com.example.puntofacil.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.puntofacil.demo.entity.Oferta;
import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.entity.Usuario;
import com.example.puntofacil.demo.repository.OfertaRepository;
import com.example.puntofacil.demo.repository.ProductoRepository;
import com.example.puntofacil.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final OfertaRepository ofertaRepository;   // ✅ nuevo

    public UsuarioController(UsuarioRepository usuarioRepository,
                             ProductoRepository productoRepository,
                             OfertaRepository ofertaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.ofertaRepository = ofertaRepository;      // ✅ nuevo
    }

    // ---------- REGISTRO ----------

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, Model model) {
        try {
            Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(usuario.getUsername());
            if (usuarioExistente.isPresent()) {
                model.addAttribute("error", "El nombre de usuario ya está en uso");
                return "usuario-registro";
            }

            usuarioRepository.save(usuario);
            return "redirect:/usuario/login?registro=exitoso";

        } catch (Exception e) {
            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            return "usuario-registro";
        }
    }

    // ---------- LOGIN / LOGOUT ----------

    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        Usuario usuarioExistente = (Usuario) session.getAttribute("usuario");
        if (usuarioExistente != null) {
            return "redirect:/usuario/productos";
        }

        model.addAttribute("usuario", new Usuario());
        return "usuario-login";
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute Usuario usuario, HttpSession session, Model model) {
        System.out.println("🔐 DEBUG: Intentando login para: " + usuario.getUsername());

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(usuario.getUsername());

        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(usuario.getPassword())) {
            session.setAttribute("usuario", usuarioOpt.get());
            System.out.println("✅ LOGIN EXITOSO para: " + usuarioOpt.get().getNombre());
            return "redirect:/usuario/productos";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "usuario-login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login?logout=true";
    }

    // ---------- VISTA PRODUCTOS + OFERTAS ----------

    @GetMapping("/productos")
    public String verProductos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/usuario/login?error=debes_iniciar_sesion";
        }

        // Productos normales
        List<Producto> productos = productoRepository.findAll();

        // Ofertas activas por fecha (si no tienen fechas, se consideran siempre activas)
        LocalDate hoy = LocalDate.now();
        List<Oferta> ofertasActivas = ofertaRepository.findAll().stream()
                .filter(o -> {
                    LocalDate ini = o.getFechaInicio();
                    LocalDate fin = o.getFechaFin();
                    boolean empiezaOk = (ini == null || !hoy.isBefore(ini)); // hoy >= ini
                    boolean terminaOk = (fin == null || !hoy.isAfter(fin));  // hoy <= fin
                    return empiezaOk && terminaOk;
                })
                .toList();

        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", productos);
        model.addAttribute("ofertas", ofertasActivas);  // ✅ se mandan a la vista

        return "usuario-productos";
    }
}
