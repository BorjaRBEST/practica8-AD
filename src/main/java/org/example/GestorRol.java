package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona las operaciones relacionadas con los roles en la base de datos.
 */
public class GestorRol {

    private final SessionFactory sessionFactory;
    private final Scanner scanner;

    public GestorRol(SessionFactory sessionFactory, Scanner scanner) {
        this.sessionFactory = sessionFactory;
        this.scanner = scanner;
    }

    /**
     * Agrega un nuevo rol a la base de datos.
     */
    public void agregarRol() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Capturar nombre del nuevo rol
            System.out.print("Nombre del Rol: ");
            String nombre = scanner.nextLine();

            // Crear y guardar el nuevo rol
            Rol rol = new Rol();
            rol.setNombre(nombre);

            session.save(rol);

            transaction.commit();
            System.out.println("Rol agregado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra todos los roles almacenados en la base de datos.
     */
    public void mostrarTodosLosRoles() {
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

    /**
     * Busca y muestra roles por su nombre.
     */
    public void buscarRolPorNombre() {
        System.out.print("Ingrese el nombre a buscar: ");
        String nombre = scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            List<Rol> roles = session.createQuery("FROM Rol WHERE nombre = :nombre", Rol.class)
                    .setParameter("nombre", nombre)
                    .list();

            if (roles.isEmpty()) {
                System.out.println("No se encontró ningún rol con ese nombre.");
            } else {
                System.out.println("Listado de Roles con nombre '" + nombre + "':");
                for (Rol rol : roles) {
                    System.out.println("ID: " + rol.getId() + ", Nombre: " + rol.getNombre());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifica la información de un rol existente.
     */
    public void modificarRol() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Capturar ID del rol a modificar
            System.out.print("Ingrese el ID del Rol a modificar: ");
            long rolId = scanner.nextLong();
            scanner.nextLine(); // Consumir salto de línea

            Rol rol = session.get(Rol.class, rolId);

            if (rol != null) {
                // Capturar nuevo nombre para el rol
                System.out.print("Nuevo Nombre del Rol: ");
                String nuevoNombre = scanner.nextLine();

                // Actualizar la información del rol
                rol.setNombre(nuevoNombre);

                session.update(rol);

                System.out.println("Rol modificado correctamente.");
            } else {
                System.out.println("Rol con ID " + rolId + " no encontrado.");
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina un rol de la base de datos.
     */
    public void eliminarRol() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Capturar ID del rol a eliminar
            System.out.print("Ingrese el ID del Rol a eliminar: ");
            long rolId = scanner.nextLong();
            scanner.nextLine(); // Consumir salto de línea

            Rol rol = session.get(Rol.class, rolId);

            if (rol != null) {
                // Eliminar el rol
                session.delete(rol);
                System.out.println("Rol eliminado correctamente.");
            } else {
                System.out.println("Rol con ID " + rolId + " no encontrado.");
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
