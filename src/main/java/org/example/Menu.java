package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Scanner;

public class Menu {

    private static Menu instance;
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private Scanner scanner;
    private GestorUsuario gestorUsuario;
    private GestorRol gestorRol;

    private Menu() {
        scanner = new Scanner(System.in);
        gestorUsuario = new GestorUsuario(sessionFactory, scanner);
        gestorRol = new GestorRol(sessionFactory, scanner);
    }

    public static synchronized Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void mostrarMenu() {
        while (true) {
            System.out.println("*****BBDD USUARIOS y ROLES*****");
            System.out.println("-------------------------------");
            System.out.println("1. Gestión de Usuarios");
            System.out.println("2. Gestión de Roles");
            System.out.println("3. Salir");

            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consume el salto de línea

            switch (opcion) {
                case 1:
                    mostrarMenuUsuarios();
                    break;
                case 2:
                    mostrarMenuRoles();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void mostrarMenuUsuarios() {
        while (true) {
            System.out.println("*****GESTIÓN DE USUARIOS*****");
            System.out.println("1. Agregar Usuario");
            System.out.println("2. Modificar Usuario");
            System.out.println("3. Eliminar Usuario");
            System.out.println("4. Mostrar Todos los Usuarios");
            System.out.println("5. Buscar Usuario por Nombre");
            System.out.println("6. Mostrar Roles de Usuario");
            System.out.println("7. Volver al Menú Principal");

            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consume el salto de línea

            switch (opcion) {
                case 1:
                    gestorUsuario.agregarUsuario();
                    break;
                case 2:
                    gestorUsuario.modificarUsuario();
                    break;
                case 3:
                    gestorUsuario.eliminarUsuario();
                    break;
                case 4:
                    gestorUsuario.mostrarTodosLosUsuarios();
                    break;
                case 5:
                    gestorUsuario.buscarUsuarioPorNombre();
                    break;
                case 6:
                    gestorUsuario.mostrarRolesDeUsuario();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void mostrarMenuRoles() {
        while (true) {
            System.out.println("*****GESTIÓN DE ROLES*****");
            System.out.println("1. Agregar Rol");
            System.out.println("2. Modificar Rol");
            System.out.println("3. Eliminar Rol");
            System.out.println("4. Mostrar Todos los Roles");
            System.out.println("5. Buscar Rol por Nombre");
            System.out.println("6. Volver al Menú Principal");

            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consume el salto de línea

            switch (opcion) {
                case 1:
                    gestorRol.agregarRol();
                    break;
                case 2:
                    gestorRol.modificarRol();
                    break;
                case 3:
                    gestorRol.eliminarRol();
                    break;
                case 4:
                    gestorRol.mostrarTodosLosRoles();
                    break;
                case 5:
                    gestorRol.buscarRolPorNombre();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }
}
