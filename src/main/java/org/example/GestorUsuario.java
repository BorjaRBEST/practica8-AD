package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.swing.*;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona las operaciones relacionadas con los usuarios en la base de datos.
 */
public class GestorUsuario {

    private final SessionFactory sessionFactory;
    private final Scanner scanner;

    public GestorUsuario(SessionFactory sessionFactory, Scanner scanner) {
        this.sessionFactory = sessionFactory;
        this.scanner = scanner;
    }

    /**
     * Agrega un nuevo usuario a la base de datos.
     */
    public void agregarUsuario() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Capturar datos del nuevo usuario
            System.out.print("Nombre del Usuario: ");
            String nombre = scanner.nextLine();

            System.out.print("Edad del Usuario: ");
            int edad = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            // Mostrar menú de selección de roles
            System.out.println("Seleccione el Rol del Usuario:");
            System.out.println("1. Admin");
            System.out.println("2. Moderador");
            System.out.println("3. Usuario Regular");
            System.out.print("Seleccione una opción: ");
            int opcionRol = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            Rol rol = obtenerRolPorNombre(session, obtenerNombreRol(opcionRol));

            // Crear y guardar el nuevo usuario
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

    /**
     * Muestra todos los usuarios almacenados en la base de datos.
     */
    public void mostrarTodosLosUsuarios() {
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

    /**
     * Busca y muestra usuarios por su nombre.
     */
    public void buscarUsuarioPorNombre() {
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

    /**
     * Modifica la información de un usuario existente.
     */
    public void modificarUsuario() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.print("Ingrese el ID del Usuario a modificar: ");
            long userId = scanner.nextLong();
            scanner.nextLine(); // Consumir salto de línea

            Usuario usuario = session.get(Usuario.class, userId);

            if (usuario != null) {
                // Capturar nuevos datos para el usuario
                System.out.print("Nuevo Nombre del Usuario: ");
                String nuevoNombre = scanner.nextLine();

                System.out.print("Nueva Edad del Usuario: ");
                int nuevaEdad = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                // Actualizar la información del usuario
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

    /**
     * Elimina un usuario de la base de datos.
     */
    public void eliminarUsuario() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.print("Ingrese el ID del Usuario a eliminar: ");
            long userId = scanner.nextLong();
            scanner.nextLine(); // Consumir salto de línea

            Usuario usuario = session.get(Usuario.class, userId);

            if (usuario != null) {
                // Eliminar el usuario
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

    /**
     * Muestra los roles asociados a un usuario específico.
     */
    public void mostrarRolesDeUsuario() {
        try (Session session = sessionFactory.openSession()) {
            System.out.print("Ingrese el ID del Usuario para ver sus roles: ");
            long userId = scanner.nextLong();
            scanner.nextLine(); // Consumir salto de línea

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

    /**
     * Obtiene una lista de información de usuarios con sus roles.
     *
     * @return DefaultListModel con información de usuarios y roles.
     */
    public DefaultListModel<String> obtenerInfoUsuariosConRoles() {
        DefaultListModel<String> model = new DefaultListModel<>();

        try (Session session = sessionFactory.openSession()) {
            List<Usuario> usuarios = session.createQuery("FROM Usuario", Usuario.class).list();

            if (usuarios.isEmpty()) {
                model.addElement("No hay usuarios registrados.");
            } else {
                for (Usuario usuario : usuarios) {
                    String userInfo = "ID: " + usuario.getId() + ", Nombre: " + usuario.getNombre() +
                            ", Edad: " + usuario.getEdad() + ", Roles: " + obtenerNombresRoles(usuario.getRoles());
                    model.addElement(userInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addElement("Error al obtener la información de los usuarios.");
        }

        return model;
    }

    /**
     * Obtiene los nombres de los roles asociados a un usuario.
     *
     * @param roles Lista de roles asociados al usuario.
     * @return String con los nombres de los roles separados por coma.
     */
    private String obtenerNombresRoles(List<Rol> roles) {
        StringBuilder sb = new StringBuilder();
        for (Rol rol : roles) {
            sb.append(rol.getNombre()).append(", ");
        }
        // Elimina la coma final y espacio si hay roles
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    /**
     * Obtiene un objeto Rol a partir de su nombre.
     *
     * @param session   Sesión de Hibernate.
     * @param nombreRol Nombre del rol a buscar.
     * @return Objeto Rol encontrado.
     */
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

    /**
     * Obtiene el nombre del rol según la opción seleccionada por el usuario.
     *
     * @param opcionRol Opción seleccionada por el usuario.
     * @return Nombre del rol correspondiente a la opción.
     */
    private String obtenerNombreRol(int opcionRol) {
        switch (opcionRol) {
            case 1:
                return "Admin";
            case 2:
                return "Moderador";
            case 3:
                return "Usuario Regular";
            default:
                System.out.println("Opción no válida. Asignando rol por defecto (Usuario Regular).");
                return "Usuario Regular";
        }
    }
}
