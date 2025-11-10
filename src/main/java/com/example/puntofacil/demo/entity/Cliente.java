package com.example.puntofacil.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_comercio", nullable = false)
    private String nombreComercio;

    @Column
    private String direccion;

    @Column
    private String email;

    @Column
    private String telefono;

    // ❌ ELIMINAR esta relación si no la necesitas
    // @OneToMany(mappedBy = "cliente")
    // private List<Empleado> empleados;

    // ==================
    // GETTERS y SETTERS
    // ==================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreComercio() {
        return nombreComercio;
    }

    public void setNombreComercio(String nombreComercio) {
        this.nombreComercio = nombreComercio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // ❌ ELIMINAR estos getters/setters también
    // public List<Empleado> getEmpleados() {
    //     return empleados;
    // }

    // public void setEmpleados(List<Empleado> empleados) {
    //     this.empleados = empleados;
    // }
}