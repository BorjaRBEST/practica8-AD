package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Clase de utilidad para gestionar la conexión con la base de datos mediante Hibernate.
 */
public class Conexion {

    // La sesión de Hibernate permite interactuar con la base de datos
    private static final SessionFactory sessionFactory;

    // Bloque estático para inicializar la sesión de Hibernate al cargar la clase
    static {
        try {
            // Configuración de Hibernate desde el archivo hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure();

            // Construcción de la sesión de Hibernate
            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            // Manejo de errores en la inicialización
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Método para obtener la sesión de Hibernate.
     *
     * @return La sesión de Hibernate.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
