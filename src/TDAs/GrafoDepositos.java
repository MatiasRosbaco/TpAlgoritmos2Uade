package TDAs;

import java.util.*;

/**
 * Grafo no dirigido con pesos que modela la red de rutas entre depósitos.
 * Utiliza lista de adyacencia. Implementa Dijkstra para distancia mínima.
 */
public class GrafoDepositos {

    // ─────────────────────────────────────────────
    //  Estructuras internas
    // ─────────────────────────────────────────────

    /** Representa una arista: destino + peso (km o saltos). */
    private static class Arista {
        final int destino;
        final int peso;

        Arista(int destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    /** Nodo auxiliar para la cola de prioridad de Dijkstra. */
    private static class NodoDijkstra implements Comparable<NodoDijkstra> {
        final int id;
        final int distancia;

        NodoDijkstra(int id, int distancia) {
            this.id = id;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDijkstra otro) {
            return Integer.compare(this.distancia, otro.distancia);
        }
    }

    // ─────────────────────────────────────────────
    //  Estado del grafo
    // ─────────────────────────────────────────────

    private final Map<Integer, List<Arista>> listaAdyacencia;

    public GrafoDepositos() {
        this.listaAdyacencia = new HashMap<>();
    }

    // ─────────────────────────────────────────────
    //  Construcción
    // ─────────────────────────────────────────────

    /** Agrega un vértice (depósito) al grafo si no existe. */
    public void agregarVertice(int idDeposito) {
        listaAdyacencia.putIfAbsent(idDeposito, new ArrayList<>());
    }

    /**
     * Agrega una ruta bidireccional entre dos depósitos.
     *
     * @param origen  ID del depósito origen
     * @param destino ID del depósito destino
     * @param peso    distancia en km (o número de saltos si peso = 1)
     */
    public void agregarRuta(int origen, int destino, int peso) {
        agregarVertice(origen);
        agregarVertice(destino);
        listaAdyacencia.get(origen).add(new Arista(destino, peso));
        listaAdyacencia.get(destino).add(new Arista(origen, peso));
    }

    // ─────────────────────────────────────────────
    //  Algoritmo de Dijkstra
    // ─────────────────────────────────────────────

    /**
     * Calcula la distancia mínima (en km) entre dos depósitos usando Dijkstra.
     *
     * @param origen  ID del depósito de partida
     * @param destino ID del depósito de llegada
     * @return distancia mínima, o -1 si no hay camino
     */
    public int distanciaMinima(int origen, int destino) {
        validarVertice(origen);
        validarVertice(destino);

        Map<Integer, Integer> distancias = inicializarDistancias(origen);
        PriorityQueue<NodoDijkstra> colaPrioridad = new PriorityQueue<>();
        colaPrioridad.offer(new NodoDijkstra(origen, 0));

        while (!colaPrioridad.isEmpty()) {
            NodoDijkstra actual = colaPrioridad.poll();

            if (actual.distancia > distancias.get(actual.id)) continue;
            if (actual.id == destino) break;

            relajarVecinos(actual, distancias, colaPrioridad);
        }

        int resultado = distancias.get(destino);
        return resultado == Integer.MAX_VALUE ? -1 : resultado;
    }

    /**
     * Calcula el camino mínimo y retorna la secuencia de IDs de depósitos.
     *
     * @param origen  ID del depósito de partida
     * @param destino ID del depósito de llegada
     * @return lista ordenada de IDs que forman el camino, vacía si no hay camino
     */
    public List<Integer> caminoMinimo(int origen, int destino) {
        validarVertice(origen);
        validarVertice(destino);

        Map<Integer, Integer> distancias = inicializarDistancias(origen);
        Map<Integer, Integer> predecesores = new HashMap<>();
        PriorityQueue<NodoDijkstra> colaPrioridad = new PriorityQueue<>();
        colaPrioridad.offer(new NodoDijkstra(origen, 0));

        for (int id : listaAdyacencia.keySet()) predecesores.put(id, -1);

        while (!colaPrioridad.isEmpty()) {
            NodoDijkstra actual = colaPrioridad.poll();

            if (actual.distancia > distancias.get(actual.id)) continue;
            if (actual.id == destino) break;

            for (Arista arista : listaAdyacencia.get(actual.id)) {
                int nuevaDist = distancias.get(actual.id) + arista.peso;
                if (nuevaDist < distancias.get(arista.destino)) {
                    distancias.put(arista.destino, nuevaDist);
                    predecesores.put(arista.destino, actual.id);
                    colaPrioridad.offer(new NodoDijkstra(arista.destino, nuevaDist));
                }
            }
        }

        return reconstruirCamino(predecesores, origen, destino);
    }

    // ─────────────────────────────────────────────
    //  Helpers privados
    // ─────────────────────────────────────────────

    private Map<Integer, Integer> inicializarDistancias(int origen) {
        Map<Integer, Integer> dist = new HashMap<>();
        for (int id : listaAdyacencia.keySet()) dist.put(id, Integer.MAX_VALUE);
        dist.put(origen, 0);
        return dist;
    }

    private void relajarVecinos(NodoDijkstra actual,
                                Map<Integer, Integer> distancias,
                                PriorityQueue<NodoDijkstra> cola) {
        for (Arista arista : listaAdyacencia.get(actual.id)) {
            if (distancias.get(actual.id) == Integer.MAX_VALUE) continue;
            int nuevaDist = distancias.get(actual.id) + arista.peso;
            if (nuevaDist < distancias.get(arista.destino)) {
                distancias.put(arista.destino, nuevaDist);
                cola.offer(new NodoDijkstra(arista.destino, nuevaDist));
            }
        }
    }

    private List<Integer> reconstruirCamino(Map<Integer, Integer> predecesores,
                                            int origen, int destino) {
        LinkedList<Integer> camino = new LinkedList<>();
        int actual = destino;

        while (actual != -1) {
            camino.addFirst(actual);
            if (actual == origen) break;
            actual = predecesores.getOrDefault(actual, -1);
        }

        if (camino.isEmpty() || camino.getFirst() != origen) {
            return Collections.emptyList();
        }
        return camino;
    }

    private void validarVertice(int id) {
        if (!listaAdyacencia.containsKey(id)) {
            throw new IllegalArgumentException("El depósito con ID " + id + " no existe en el grafo.");
        }
    }

    // ─────────────────────────────────────────────
    //  Utilidades de visualización
    // ─────────────────────────────────────────────

    /** Imprime la lista de adyacencia completa del grafo. */
    public void imprimirGrafo() {
        System.out.println("=== Red de Rutas (Lista de Adyacencia) ===");
        listaAdyacencia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    System.out.print("  Depósito " + entry.getKey() + " → ");
                    entry.getValue().forEach(a ->
                            System.out.print("[Dep." + a.destino + ", " + a.peso + "km] "));
                    System.out.println();
                });
    }

    public Set<Integer> getVertices() {
        return Collections.unmodifiableSet(listaAdyacencia.keySet());
    }
}
