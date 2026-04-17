import com.google.gson.*;
import java.io.*;
import java.util.*;

public class Persistencia {

    public static List<Paquete<String>> cargarDesdeJson(String ruta) {
        List<Paquete<String>> paquetes = new ArrayList<>();
        try (FileReader reader = new FileReader(ruta)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();

                int id = obj.get("id").getAsInt();
                double peso = obj.get("peso").getAsDouble();
                String destino = obj.get("destino").getAsString();
                String contenido = obj.get("contenido").getAsString();
                String prio = obj.get("prioridad").getAsString().toUpperCase();
                Paquete.Prioridad prioridad = prio.equals("URGENTE")
                        ? Paquete.Prioridad.URGENTE : Paquete.Prioridad.ESTANDAR;

                paquetes.add(new Paquete<>(id, peso, destino, contenido, prioridad));
            }
            System.out.println("  ✓ Se cargaron " + paquetes.size() + " paquetes desde " + ruta);
        } catch (Exception e) {
            System.out.println("  ✗ Error al leer " + ruta + ": " + e.getMessage());
        }
        return paquetes;
    }
}