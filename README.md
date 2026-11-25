# Tarea 3 – MiniFacebook

**Curso:** Estructuras de Datos – Ingeniería Civil Informática  
**Universidad:** Universidad del Bío-Bío  
**Integrantes:** Vicente Sepúlveda Y Cristobal Poblete


Programa de consola en Java que simula una red social sencilla. Permite registrar y editar personas, representar sus amistades mediante un grafo no dirigido y realizar consultas sobre esas relaciones.

## Requisitos

- Java JDK 8 o superior.
- Terminal o consola de comandos.

## Archivos

- `src/MiniFacebook.java`: contiene el programa completo, las clases `Persona` y `Amistad`, las operaciones del grafo y el menú principal.

## Cómo compilar y ejecutar

Abra una terminal en la carpeta donde se encuentra `src/MiniFacebook.java` y ejecute:

```bash
javac MiniFacebook.java
java MiniFacebook
```

En Windows también puede ejecutar los mismos comandos desde CMD o PowerShell, siempre que el JDK esté instalado y configurado en el PATH.

## Opciones del menú

| Opción | Función |
|---|---|
| 1 | Insertar una persona. El ID se asigna automáticamente de forma secuencial. |
| 2 | Editar los datos básicos de una persona. Los campos vacíos conservan el valor anterior. |
| 3 | Listar todas las personas registradas. |
| 4 | Agregar una amistad entre dos personas e imprimir las notificaciones simuladas para sus amigos directos. |
| 5 | Bloquear una amistad existente. La relación deja de considerarse en las consultas. |
| 6 | Buscar cumpleaños dentro de los próximos `k` días e imprimir las notificaciones para sus amigos directos. |
| 7 | Calcular el nivel mínimo de amistad entre dos personas mediante BFS. |
| 8 | Mostrar los amigos directos comunes de dos personas. |
| 9 | Encontrar la(s) persona(s) con mayor cantidad de amigos alcanzables hasta nivel 3. |
| 10 | Cargar un conjunto pequeño de datos de ejemplo. |
| 0 | Salir del programa. |

## Estructuras de datos y algoritmos

- **Personas:** se almacenan en un `LinkedHashMap<Integer, Persona>`, usando el ID como clave.
- **Grafo:** se representa mediante listas de adyacencia implementadas con mapas (`Map<Integer, Map<Integer, Amistad>>`). Es no dirigido: una amistad se registra en ambos sentidos.
- **Amistad:** almacena la fecha de inicio y los datos del bloqueo.
- **BFS (búsqueda en anchura):** se utiliza para obtener el nivel mínimo de amistad y para contar las personas alcanzables hasta nivel 3.
- **Amigos comunes:** se obtiene la intersección de los conjuntos de amigos directos de ambas personas.

