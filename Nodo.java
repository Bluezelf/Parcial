public class Nodo implements Runnable {
    private Persona[] personas;
    private String nombre;

    public Nodo(Persona[] personas, String nombre) {
        this.personas = personas;
        this.nombre = nombre;
    }

    @Override
    public void run() {
        System.out.println("Nodo " + nombre + " procesando datos...");
        for (Persona p : personas) {
            if (p.ingresosMensuales > 30000) {
                System.out.println(p.nombre + " tiene un riesgo bajo por ingresos altos.");
            } else if (p.edad < 40) {
                System.out.println(p.nombre + " tiene un riesgo bajo por edad.");
            }
        }
    }
}
