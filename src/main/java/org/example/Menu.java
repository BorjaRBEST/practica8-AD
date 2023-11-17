package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.NoResultException;
import java.util.List;
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

    private Menu() {
        scanner = new Scanner(System.in);
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
            System.out.println("1. Añadir Usuario");
            System.out.println("2. Modificar Usuario");
            System.out.println("3. Eliminar Usuario");
            System.out.println("4. Mostrar Todos los Usuarios");
            System.out.println("5. Buscar Usuario por Nombre");
            System.out.println("6. Mostrar Todos los Roles");
            System.out.println("7. Mostrar Roles y Usuarios asignados");
            System.out.println("8. Salir");

            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consume el salto de línea

            switch (opcion) {
                case 1:
                    agregarUsuario();
                    break;
                case 2:
                    modificarUsuario();
                    break;
                case 3:
                    eliminarUsuario();
                    break;
                case 4:
                    mostrarTodosLosUsuarios();
                    break;
                case 5:
                    buscarUsuarioPorNombre();
                    break;
                case 6:
                    mostrarTodosLosRoles();
                    break;
                case 7:
                    mostrarRolesDeUsuario();
                    break;
                case 8:
                    System.out.println("Saliendo...");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void agregarUsuario() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.print("Nombre del Usuario: ");
            String nombre = scanner.nextLine();

            System.out.print("Edad del Usuario: ");
            int edad = scanner.nextInt();
            scanner.nextLine(); // Consume el salto de línea

            // Mostrar menú de selección de roles
            System.out.println("Seleccione el Rol del Usuario:");
            System.out.println("1. Admin");
            System.out.println("2. Moderador");
            System.out.println("3. Usuario Regular");
            System.out.print("Seleccione una opción: ");
            int opcionRol = scanner.nextInt();
            scanner.nextLine(); // Consume el salto de línea

            Rol rol;
            switch (opcionRol) {
                case 1:
                    rol = obtenerRolPorNombre(session, "Admin");
                    break;
                case 2:
                    rol = obtenerRolPorNombre(session, "Moderador");
                    break;
                case 3:
                    rol = obtenerRolPorNombre(session, "Usuario Regular");
                    break;
                default:
                    System.out.println("Opción no válida. Asignando rol por defecto (Usuario Regular).");
                    rol = obtenerRolPorNombre(session, "Usuario Regular");
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEdad(edad);
            usuario.setRoles(List.of(rol));

            session.save(usuario);

            transaction.commit();
            System.out.println("Usuario agregado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Rol obtenerRolPorNombre(Session session, String nombreRol) {
        try {
            return session.createQuery("FROM Rol WHERE nombre = :nombre", Rol.class)
                    .setParameter("nombre", nombreRol)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Rol '" + nombreRol + "' no encontrado. Asignando rol por defecto (Usuario Regular).");
            return session.createQuery("FROM Rol WHERE nombre = 'Usuario Regular'", Rol.class)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void modificarUsuario() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.print("Ingrese el ID del Usuario a modificar: ");
            long userId = scanner.nextLong();
            scanner.nextLine(); // Consume el salto de línea

            Usuario usuario = session.get(Usuario.class, userId);

            if (usuario != null) {
                System.out.print("Nuevo Nombre del Usuario: ");
                String nuevoNombre = scanner.nextLine();

                System.out.print("Nueva Edad del Usuario: ");
                int nuevaEdad = scanner.nextInt();
                scanner.nextLine(); // Consume el salto de línea

                usuario.setNombre(nuevoNombre);
                usuario.setEdad(nuevaEdad);

                session.update(usuario);

                System.out.println("Usuario modificado correctamente.");
            } else {
                System.out.println("Usuario con ID " + userId + " no encontrado.");
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarUsuario() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.print("Ingrese el ID del Usuario a eliminar: ");
            long userId = scanner.nextLong();
            scanner.nextLine(); // Consume el salto de línea

            Usuario usuario = session.get(Usuario.class, userId);

            if (usuario != null) {
                session.delete(usuario);
                System.out.println("Usuario eliminado correctamente.");
            } else {
                System.out.println("Usuario con ID " + userId + " no encontrado.");
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarTodosLosUsuarios() {
        try (Session session = sessionFactory.openSession()) {
            List<Usuario> usuarios = session.createQuery("FROM Usuario", Usuario.class).list();

            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
            } else {
                System.out.println("Listado de Usuarios:");
                for (Usuario usuario : usuarios) {
                    System.out.println("ID: " + usuario.getId() + ", Nombre: " + usuario.getNombre() + ", Edad: " + usuario.getEdad());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buscarUsuarioPorNombre() {
        System.out.print("Ingrese el nombre a buscar: ");
        String nombre = scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            List<Usuario> usuarios = session.createQuery("FROM Usuario WHERE nombre = :nombre", Usuario.class)
                    .setParameter("nombre", nombre)
                    .list();

            if (usuarios.isEmpty()) {
                System.out.println("No se encontró ningún usuario con ese nombre.");
            } else {
                System.out.println("Listado de Usuarios con nombre '" + nombre + "':");
                for (Usuario usuario : usuarios) {
                    System.out.println("ID: " + usuario.getId() + ", Nombre: " + usuario.getNombre() + ", Edad: " + usuario.getEdad());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarTodosLosRoles() {
        try (Session session = sessionFactory.openSession()) {
            List<Rol> roles = session.createQuery("FROM Rol", Rol.class).list();

            if (roles.isEmpty()) {
                System.out.println("No hay roles registrados.");
            } else {
                System.out.println("Listado de Roles:");
                for (Rol rol : roles) {
                    System.out.println("ID: " + rol.getId() + ", Nombre: " + rol.getNombre());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void mostrarRolesDeUsuario() {
        try (Session session = sessionFactory.openSession()) {
            System.out.print("Ingrese el ID del Usuario para ver sus roles: ");
            long userId = scanner.nextLong();
            scanner.nextLine(); // Consume el salto de línea

            Usuario usuario = session.get(Usuario.class, userId);

            if (usuario != null) {
                List<Rol> roles = usuario.getRoles();

                if (roles.isEmpty()) {
                    System.out.println("El usuario no tiene roles asignados.");
                } else {
                    System.out.println("Roles del Usuario " + usuario.getNombre() + ":");
                    for (Rol rol : roles) {
                        System.out.println("ID: " + rol.getId() + ", Nombre: " + rol.getNombre());
                    }
                }
            } else {
                System.out.println("Usuario con ID " + userId + " no encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}