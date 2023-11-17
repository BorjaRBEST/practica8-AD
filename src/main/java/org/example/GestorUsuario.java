package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Scanner;

public class GestorUsuario {

    private final SessionFactory sessionFactory;
    private final Scanner scanner;

    public GestorUsuario(SessionFactory sessionFactory, Scanner scanner) {
        this.sessionFactory = sessionFactory;
        this.scanner = scanner;
    }

    public void agregarUsuario() {
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

            Rol rol = obtenerRolPorNombre(session, obtenerNombreRol(opcionRol));

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

    public void modificarUsuario() {
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

    public void eliminarUsuario() {
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

    public void mostrarRolesDeUsuario() {
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
