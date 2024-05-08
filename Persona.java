import java.io.Serializable;

public class Persona implements Serializable {
    private static final long serialVersionUID = 1L;  // Añade un UID de versión

    String nombre;
    int edad;
    int ingresosMensuales;

    public Persona(String nombre, int edad, int ingresosMensuales) {
        this.nombre = nombre;
        this.edad = edad;
        this.ingresosMensuales = ingresosMensuales;
    }
}
