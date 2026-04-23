import com.google.gson.*;
import java.io.*;
import java.util.*;

/**
 * Clase utilitaria para cargar paquetes desde un archivo JSON.
 *
 * ┌────────────────────────────────────────────────────────────────────────┐
 * │  RESUMEN DE COMPLEJIDAD – Persistencia                               │
 * ├──────────────────────────┬─────────────────────────────────────────────┤
 * │ cargarDesdeJson()        │ O(n) – donde n = cantidad de objetos JSON  │
 * └──────────────────────────┴─────────────────────────────────────────────┘
 */
public class Persistencia {

    /**
     * Lee un archivo JSON y construye una lista de paquetes.
     *
     * Complejidad: O(n), donde n = cantidad de elementos en el array JSON.
     *
     * Desglose:
     *   1. parseReader()      → O(m) donde m = tamaño del archivo en caracteres.
     *      El parser de Gson recorre el archivo una vez para construir el
     *      árbol JsonElement (lectura secuencial).
     *
     *   2. Bucle for-each     → O(n) iteraciones, cada una con:
     *        - obj.get(key)   → O(1) amortizado (HashMap interno del JsonObject).
     *        - getAsInt/String/Double → O(1) (conversión directa del valor).
     *        - equals("URGENTE") → O(1) (cadena de longitud fija).
     *        - new Paquete()  → O(1).
     *        - paquetes.add() → O(1) amortizado (ArrayList puede hacer resize).
     *      Total por iteración: O(1) → total del bucle: O(n).
     *
     *   3. Complejidad total  → O(m + n).
     *      Como m ≥ n (cada paquete ocupa al menos un carácter), esto es O(m).
     *      Si consideramos n como la métrica principal: O(n), asumiendo
     *      que el tamaño promedio de cada entrada JSON es constante.
     *
     * Complejidad espacial: O(n) – se almacenan n objetos Paquete en la lista,
     *   más el árbol JSON completo en memoria durante el parseo.
     */
    public static List<Paquete<String>> cargarDesdeJson(String ruta) {
        List<Paquete<String>> paquetes = new ArrayList<>();  // O(1)
        try (FileReader reader = new FileReader(ruta)) {     // O(1)
            JsonArray array = JsonParser.parseReader(reader)  // O(m) – parseo del archivo
                    .getAsJsonArray();

            for (JsonElement elem : array) {                  // O(n) iteraciones
                JsonObject obj = elem.getAsJsonObject();      // O(1)

                int id = obj.get("id").getAsInt();            // O(1)
                double peso = obj.get("peso").getAsDouble();  // O(1)
                String destino = obj.get("destino").getAsString();     // O(1)
                String contenido = obj.get("contenido").getAsString(); // O(1)
                String prio = obj.get("prioridad").getAsString().toUpperCase(); // O(1)
                Paquete.Prioridad prioridad = prio.equals("URGENTE")
                        ? Paquete.Prioridad.URGENTE : Paquete.Prioridad.ESTANDAR; // O(1)

                paquetes.add(new Paquete<>(id, peso, destino, contenido, prioridad)); // O(1) amort.
            }
            System.out.println("  ✓ Se cargaron " + paquetes.size() + " paquetes desde " + ruta);
        } catch (Exception e) {
            System.out.println("  ✗ Error al leer " + ruta + ": " + e.getMessage());
        }
        return paquetes;
    }
}