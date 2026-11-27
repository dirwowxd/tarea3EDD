import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.*;

//Vicente Sepúlveda y Cristobal Poblete


public class MiniFacebook {
    private final Map<Integer, Persona> personas = new LinkedHashMap<>();
    private final Map<Integer, Map<Integer, Amistad>> grafo = new HashMap<>();
    private int siguienteId = 1;
    private final Scanner sc = new Scanner(System.in);

    static class Persona {
        int id;
        String nombre;
        int diaNacimiento;
        int mesNacimiento;
        String actividad;
        String email;

        Persona(int id, String nombre, int dia, int mes, String actividad, String email) {
            this.id = id;
            this.nombre = nombre;
            this.diaNacimiento = dia;
            this.mesNacimiento = mes;
            this.actividad = actividad;
            this.email = email;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | Nombre: " + nombre
                    + " | Nacimiento: " + String.format("%02d/%02d", diaNacimiento, mesNacimiento)
                    + " | Actividad: " + actividad + " | Email: " + email;
        }
    }

    static class Amistad {
        LocalDate fechaInicio;
        boolean bloqueada;
        int bloqueadaPor;
        LocalDate fechaBloqueo;

        Amistad(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            this.bloqueada = false;
        }
    }


    private void insertarPersona(String nombre, int dia, int mes, String actividad, String email) {
        if (!fechaNacimientoValida(dia, mes)) {
            System.out.println("Fecha de nacimiento no válida.");
            return;
        }
        int id = siguienteId++;
        Persona p = new Persona(id, nombre, dia, mes, actividad, email);
        personas.put(id, p);
        grafo.put(id, new HashMap<>());
        System.out.println("Persona registrada correctamente. Su ID es " + id + ".");
    }

    private boolean fechaNacimientoValida(int dia, int mes) {
        try {
            LocalDate.of(2024, mes, dia);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    private void editarPersona(int id) {
        Persona p = personas.get(id);
        if (p == null) {
            System.out.println("No existe una persona con ese ID.");
            return;
        }
        System.out.println("Deje un campo vacío para mantener su valor actual.");
        String valor;
        System.out.print("Nombre [" + p.nombre + "]: ");
        valor = sc.nextLine().trim();
        if (!valor.isEmpty()) p.nombre = valor;

        System.out.print("Día de nacimiento [" + p.diaNacimiento + "]: ");
        valor = sc.nextLine().trim();
        int dia = p.diaNacimiento;
        if (!valor.isEmpty()) dia = leerEnteroDesdeTexto(valor, dia);

        System.out.print("Mes de nacimiento [" + p.mesNacimiento + "]: ");
        valor = sc.nextLine().trim();
        int mes = p.mesNacimiento;
        if (!valor.isEmpty()) mes = leerEnteroDesdeTexto(valor, mes);
        if (fechaNacimientoValida(dia, mes)) {
            p.diaNacimiento = dia;
            p.mesNacimiento = mes;
        } else {
            System.out.println("Día/mes inválido; se mantiene la fecha anterior.");
        }

        System.out.print("Actividad/profesión [" + p.actividad + "]: ");
        valor = sc.nextLine().trim();
        if (!valor.isEmpty()) p.actividad = valor;
        System.out.print("Email [" + p.email + "]: ");
        valor = sc.nextLine().trim();
        if (!valor.isEmpty()) p.email = valor;
        System.out.println("Datos actualizados.");
    }


    private int leerEnteroDesdeTexto(String texto, int valorSiError) {
        try { return Integer.parseInt(texto); }
        catch (NumberFormatException e) {
            System.out.println("Número inválido; se conserva el valor anterior.");
            return valorSiError;
        }
    }

    private void listarPersonas() {
        if (personas.isEmpty()) {
            System.out.println("Todavía no hay personas registradas.");
            return;
        }
        for (Persona p : personas.values()) System.out.println(p);
    }


    private void agregarAmistad(int id1, LocalDate fecha, int id2) {
        if (!personas.containsKey(id1) || !personas.containsKey(id2)) {
            System.out.println("Ambos ID deben corresponder a personas registradas.");
            return;
        }
        if (id1 == id2) {
            System.out.println("Una persona no puede agregarse como su propia amiga.");
            return;
        }

        Amistad existente = grafo.get(id1).get(id2);
        if (existente != null && !existente.bloqueada) {
            System.out.println("Estas personas ya son amigas directas.");
            return;
        }

        Amistad nueva = new Amistad(fecha);
        grafo.get(id1).put(id2, nueva);
        grafo.get(id2).put(id1, nueva);
        System.out.println("Amistad registrada entre " + nombre(id1) + " y " + nombre(id2) + ".");

        imprimirSugerencias(id2, id1);
        imprimirSugerencias(id1, id2);
    }

    private void imprimirSugerencias(int personaQueRecibe, int personaSugerida) {
        for (int amigoId : amigosDirectos(personaQueRecibe)) {
            if (amigoId == personaSugerida) continue;
            Persona destinatario = personas.get(amigoId);
            System.out.println("\n--- NOTIFICACIÓN (simulación de email) ---");
            System.out.println("Para: " + destinatario.email);
            System.out.println("Fecha: " + LocalDate.now());
            System.out.println("Mensaje: Hola " + destinatario.nombre + ", quizás conozcas a "
                    + nombre(personaSugerida) + ". ¡Ahora es amigo/a de "
                    + nombre(personaQueRecibe) + "!");
        }
    }

    private void bloquearAmistad(int id1, LocalDate fecha, int id2) {
        if (!personas.containsKey(id1) || !personas.containsKey(id2) || id1 == id2) {
            System.out.println("ID inválido. Debe indicar dos personas distintas y registradas.");
            return;
        }
        Amistad amistad = grafo.get(id1).get(id2);
        if (amistad == null || amistad.bloqueada) {
            System.out.println("No existe una amistad directa activa entre esas personas.");
            return;
        }
        amistad.bloqueada = true;
        amistad.bloqueadaPor = id1;
        amistad.fechaBloqueo = fecha;
        System.out.println(nombre(id1) + " bloqueó a " + nombre(id2) + " el " + fecha + ".");
        System.out.println("La relación deja de contarse como amistad para las consultas del grafo.");
    }

    private List<Integer> amigosDirectos(int id) {
        List<Integer> resultado = new ArrayList<>();
        Map<Integer, Amistad> vecinos = grafo.get(id);
        if (vecinos == null) return resultado;
        for (Map.Entry<Integer, Amistad> entrada : vecinos.entrySet()) {
            if (!entrada.getValue().bloqueada) resultado.add(entrada.getKey());
        }
        return resultado;
    }

    private void cumpleañosProximos(int k) {
        if (k <= 0) {
            System.out.println("K debe ser mayor que cero.");
            return;
        }
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(k);
        boolean encontrado = false;

        for (Persona p : personas.values()) {
            LocalDate proximo = proximoCumpleaños(p, hoy);
            if (proximo.isAfter(hoy) && !proximo.isAfter(limite)) {
                encontrado = true;
                System.out.println("\nCumpleaños de " + p.nombre + ": " + proximo);
                for (int amigoId : amigosDirectos(p.id)) {
                    Persona amigo = personas.get(amigoId);
                    System.out.println("--- NOTIFICACIÓN (simulación de email) ---");
                    System.out.println("Para: " + amigo.email);
                    System.out.println("Mensaje: Hola " + amigo.nombre + ", tu amigo/a " + p.nombre
                            + " estará de cumpleaños el " + proximo + ". ¡No olvides saludarle!");
                }
            }
        }
        if (!encontrado) System.out.println("No hay cumpleaños en los próximos " + k + " días.");
    }

    private LocalDate proximoCumpleaños(Persona p, LocalDate hoy) {
        LocalDate fecha;
        try {
            fecha = LocalDate.of(hoy.getYear(), p.mesNacimiento, p.diaNacimiento);
        } catch (DateTimeException e) {
            fecha = LocalDate.of(hoy.getYear(), 2, 28);
        }
        if (!fecha.isAfter(hoy)) {
            try {
                fecha = LocalDate.of(hoy.getYear() + 1, p.mesNacimiento, p.diaNacimiento);
            } catch (DateTimeException e) {
                fecha = LocalDate.of(hoy.getYear() + 1, 2, 28);
            }
        }
        return fecha;
    }

    private int nivelAmistad(int origen, int destino) {
        if (!personas.containsKey(origen) || !personas.containsKey(destino)) return -1;
        if (origen == destino) return 0;

        Queue<Integer> cola = new LinkedList<>();
        Map<Integer, Integer> distancia = new HashMap<>();
        cola.add(origen);
        distancia.put(origen, 0);

        while (!cola.isEmpty()) {
            int actual = cola.poll();
            for (int vecino : amigosDirectos(actual)) {
                if (!distancia.containsKey(vecino)) {
                    int nuevaDistancia = distancia.get(actual) + 1;
                    if (vecino == destino) return nuevaDistancia;
                    distancia.put(vecino, nuevaDistancia);
                    cola.add(vecino);
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    private void amigosComunes(int id1, int id2) {
        if (!personas.containsKey(id1) || !personas.containsKey(id2) || id1 == id2) {
            System.out.println("Debe ingresar dos ID distintos y registrados.");
            return;
        }
        Set<Integer> amigos1 = new HashSet<>(amigosDirectos(id1));
        amigos1.retainAll(amigosDirectos(id2));
        if (amigos1.isEmpty()) {
            System.out.println("No tienen amigos directos en común.");
        } else {
            System.out.println("Amigos directos en común:");
            for (int id : amigos1) System.out.println(personas.get(id));
        }
    }

    private void mayorCantidadAmigosHastaNivel3() {
        if (personas.isEmpty()) {
            System.out.println("No hay personas registradas.");
            return;
        }
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        int maximo = -1;
        for (int id : personas.keySet()) {
            int cantidad = contarHastaNivel(id, 3);
            cantidades.put(id, cantidad);
            if (cantidad > maximo) maximo = cantidad;
        }
        System.out.println("Mayor cantidad de amigos considerando niveles 1, 2 y 3: " + maximo);
        System.out.println("Persona(s) que alcanzan esa cantidad:");
        for (Map.Entry<Integer, Integer> e : cantidades.entrySet()) {
            if (e.getValue() == maximo) System.out.println(personas.get(e.getKey()));
        }
    }
    private int contarHastaNivel(int origen, int nivelMaximo) {
        Queue<Integer> cola = new LinkedList<>();
        Map<Integer, Integer> distancia = new HashMap<>();
        cola.add(origen);
        distancia.put(origen, 0);
        int contador = 0;

        while (!cola.isEmpty()) {
            int actual = cola.poll();
            int nivel = distancia.get(actual);
            if (nivel == nivelMaximo) continue;
            for (int vecino : amigosDirectos(actual)) {
                if (!distancia.containsKey(vecino)) {
                    distancia.put(vecino, nivel + 1);
                    cola.add(vecino);
                    contador++;
                }
            }
        }
        return contador;
    }


    private String nombre(int id) {
        Persona p = personas.get(id);
        return p == null ? "ID " + id : p.nombre;
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            try { return Integer.parseInt(texto); }
            catch (NumberFormatException e) { System.out.println("Ingrese un número entero válido."); }
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (AAAA-MM-DD): ");
            try { return LocalDate.parse(sc.nextLine().trim()); }
            catch (DateTimeParseException e) { System.out.println("Fecha inválida. Ejemplo: 2026-11-24."); }
        }
    }

    private void cargarDatosEjemplo() {
        insertarPersona("Luis", 11, 12, "Profesor", "luis@mail.com");
        insertarPersona("Lucia", 10, 11, "Estudiante", "lucia@mail.com");
        insertarPersona("Ana", 5, 3, "Ingeniera", "ana@mail.com");
        insertarPersona("Pedro", 20, 7, "Diseñador", "pedro@mail.com");
        LocalDate hoy = LocalDate.now();
        agregarAmistad(1, hoy, 2);
        agregarAmistad(2, hoy, 3);
        agregarAmistad(1, hoy, 4);
    }

    private void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n========== MINIFACEBOOK ==========");
            System.out.println("1. Insertar persona");
            System.out.println("2. Editar persona");
            System.out.println("3. Listar personas");
            System.out.println("4. Agregar relación de amistad");
            System.out.println("5. Bloquear a un amigo");
            System.out.println("6. Cumpleaños en los próximos k días");
            System.out.println("7. Obtener nivel de amistad");
            System.out.println("8. Encontrar amigos directos comunes");
            System.out.println("9. Persona(s) con más amigos hasta nivel 3");
            System.out.println("10. Cargar datos de ejemplo");
            System.out.println("0. Salir");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    System.out.print("Nombre: "); String nombre = sc.nextLine();
                    int dia = leerEntero("Día de nacimiento: ");
                    int mes = leerEntero("Mes de nacimiento: ");
                    System.out.print("Actividad/profesión: "); String actividad = sc.nextLine();
                    System.out.print("Email: "); String email = sc.nextLine();
                    insertarPersona(nombre, dia, mes, actividad, email);
                    break;
                case 2:
                    editarPersona(leerEntero("ID de la persona: "));
                    break;
                case 3:
                    listarPersonas();
                    break;
                case 4: {
                    int p1 = leerEntero("ID de p1: ");
                    LocalDate fecha = leerFecha("Fecha de amistad");
                    int p2 = leerEntero("ID de p2: ");
                    agregarAmistad(p1, fecha, p2);
                    break;
                }
                case 5: {
                    int p1 = leerEntero("ID de quien bloquea (p1): ");
                    LocalDate fecha = leerFecha("Fecha del bloqueo");
                    int p2 = leerEntero("ID de la persona bloqueada (p2): ");
                    bloquearAmistad(p1, fecha, p2);
                    break;
                }
                case 6:
                    cumpleañosProximos(leerEntero("Ingrese k (días): "));
                    break;
                case 7: {
                    int p1 = leerEntero("ID de p1: ");
                    int p2 = leerEntero("ID de p2: ");
                    int nivel = nivelAmistad(p1, p2);
                    if (nivel == -1) System.out.println("ID no válido.");
                    else if (nivel == Integer.MAX_VALUE) System.out.println("Nivel de amistad: infinito (no existe camino).");
                    else System.out.println("Nivel mínimo de amistad: " + nivel);
                    break;
                }
                case 8:
                    amigosComunes(leerEntero("ID de p1: "), leerEntero("ID de p2: "));
                    break;
                case 9:
                    mayorCantidadAmigosHastaNivel3();
                    break;
                case 10:
                    cargarDatosEjemplo();
                    break;
                case 0:
                    System.out.println("Programa finalizado.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    public static void main(String[] args) {
        new MiniFacebook().mostrarMenu();
    }
}
