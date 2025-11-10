package com.example.puntofacil.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.entity.Usuario;
import com.example.puntofacil.demo.repository.ProductoRepository;
import com.example.puntofacil.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, ProductoRepository productoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    // Formulario de registro de usuarios
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-registro";
    }

    // Procesar registro de usuarios
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

    // Formulario de login para usuarios
    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        // Verificar si ya hay usuario en sesión
        Usuario usuarioExistente = (Usuario) session.getAttribute("usuario");
        if (usuarioExistente != null) {
            return "redirect:/usuario/productos";
        }
        
        model.addAttribute("usuario", new Usuario());
        return "usuario-login";
    }

    // Procesar login de usuarios
    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute Usuario usuario, HttpSession session, Model model) {
        System.out.println("🔐 DEBUG: Intentando login para: " + usuario.getUsername());
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(usuario.getUsername());
        
        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(usuario.getPassword())) {
            // Login exitoso
            session.setAttribute("usuario", usuarioOpt.get());
            System.out.println("✅ LOGIN EXITOSO para: " + usuarioOpt.get().getNombre());
            return "redirect:/usuario/productos";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "usuario-login";
        }
    }

    // Logout para usuarios
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login?logout=true";
    }

    // Vista de productos
    @GetMapping("/productos")
    public String verProductos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/usuario/login?error=debes_iniciar_sesion";
        }

        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", productos);

        return "usuario-productos";
    }
}