import java.io.*;
import java.util.*;

/**
 * Lee paquetes desde inventario.json (parser manual, sin librerías externas).
 */
public class Persistencia {

    public static List<Paquete<String>> cargarDesdeJson(String ruta) {
        List<Paquete<String>> paquetes = new ArrayList<>();
        try {
            String json = leerArchivo(ruta);
            List<Map<String, String>> registros = parsearArrayJson(json);

            for (Map<String, String> r : registros) {
                int id = Integer.parseInt(r.getOrDefault("id", "0"));
                double peso = Double.parseDouble(r.getOrDefault("peso", "0"));
                String destino = r.getOrDefault("destino", "SinDestino");
                String contenido = r.getOrDefault("contenido", "Sin descripción");
                String prio = r.getOrDefault("prioridad", "ESTANDAR").toUpperCase();
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

    private static String leerArchivo(String ruta) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) sb.append(linea);
        }
        return sb.toString();
    }

    private static List<Map<String, String>> parsearArrayJson(String json) {
        List<Map<String, String>> lista = new ArrayList<>();
        json = json.trim();
        if (!json.startsWith("[")) return lista;
        int i = 0;
        while (i < json.length()) {
            int start = json.indexOf('{', i);
            if (start == -1) break;
            int end = json.indexOf('}', start);
            if (end == -1) break;
            lista.add(parsearObjeto(json.substring(start + 1, end)));
            i = end + 1;
        }
        return lista;
    }

    private static Map<String, String> parsearObjeto(String obj) {
        Map<String, String> mapa = new LinkedHashMap<>();
        String[] pares = obj.split(",");
        for (String par : pares) {
            String[] kv = par.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                String val = kv[1].trim().replace("\"", "");
                mapa.put(key, val);
            }
        }
        return mapa;
    }
}