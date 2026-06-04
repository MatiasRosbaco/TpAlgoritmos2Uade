package TDAs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ABB<T extends Comparable<T>> {

    private Nodo<T> raiz;

    public ABB() {
        raiz = null;
    }

    // Agregar
    public void agregar(T elem) {
        raiz = agregar(raiz, elem);
    }

    private Nodo<T> agregar(Nodo<T> nodo, T elem) {
        // raiz si arbol vacio
        if (nodo == null) return new Nodo<>(elem);

        int cmp = elem.compareTo(nodo.elemento);
        if (cmp < 0) {
            nodo.izq = agregar(nodo.izq, elem);
        }
        else if (cmp > 0) {
            nodo.der = agregar(nodo.der, elem);
        }
        return nodo;
    }

    // Eliminar
    public void eliminar(T elem) {
        raiz = eliminar(raiz, elem);
    }

    private Nodo<T> eliminar(Nodo<T> nodo, T elem) {
        if (nodo == null) return null; // no existe

        int cmp = elem.compareTo(nodo.elemento);
        if (cmp < 0) {
            nodo.izq = eliminar(nodo.izq, elem);
        } else if (cmp > 0) {
            nodo.der = eliminar(nodo.der, elem);
        } else {
            // Caso 1: nodo hoja o con un solo hijo
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;

            // Caso 2: nodo con dos hijos
            // Se reemplaza con el mínimo del subárbol derecho
            nodo.elemento = minimo(nodo.der);
            nodo.der = eliminar(nodo.der, nodo.elemento);
        }
        return nodo;
    }

    // Buscar nodo

    public boolean buscar(T elem) {
        return buscar(raiz, elem);
    }

    private boolean buscar(Nodo<T> nodo, T elem) {
        if (nodo == null) return false;
        int cmp = elem.compareTo(nodo.elemento);
        if (cmp == 0) return true;
        if (cmp < 0) return buscar(nodo.izq, elem);
        return buscar(nodo.der, elem);
    }

    // Recorridos

    public void preorden() {
        System.out.print("Preorden: ");
        preorden(raiz);
        System.out.println();
    }

    private void preorden(Nodo<T> nodo) {
        if (nodo == null) return;
        System.out.print(nodo.elemento + "  ");  // raíz → izq → der
        preorden(nodo.izq);
        preorden(nodo.der);
    }

    public void inorden() {
        System.out.print("Inorden: ");
        inorden(raiz);
        System.out.println();
    }

    private void inorden(Nodo<T> nodo) {
        if (nodo == null) return;
        inorden(nodo.izq);
        System.out.print(nodo.elemento + "  ");  // izq → raíz → der
        inorden(nodo.der);
    }

    public void postorden() {
        System.out.print("Postorden: ");
        postorden(raiz);
        System.out.println();
    }

    private void postorden(Nodo<T> nodo) {
        if (nodo == null) return;
        postorden(nodo.izq);
        postorden(nodo.der);
        System.out.print(nodo.elemento + "  ");  // izq → der → raíz
    }

    // Arbol vacio
    public boolean esVacio() {
        return raiz == null;
    }

    // get Raiz
    public T obtenerRaiz() {
        if (raiz == null) return null;
        return raiz.elemento;
    }

    // ---------------------------------------------------------------
    // Mínimo
    // ---------------------------------------------------------------

    public T minimo() {
        if (raiz == null) return null;
        return minimo(raiz);
    }

    // Recursividad hacia lo maximo a la izquierda ya que es el minimo
    private T minimo(Nodo<T> nodo) {
        if (nodo.izq == null) return nodo.elemento;
        return minimo(nodo.izq);
    }

    // ---------------------------------------------------------------
    // AUDITORÍA — recorrido post-orden
    //
    // Marca visitado=true en los nodos que NO fueron auditados en los
    // últimos 30 días (es decir, fechaUltimaAuditoria es null, o está
    // a más de 30 días del momento actual).
    // ---------------------------------------------------------------

    public void auditarPendientes() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        auditarPostOrden(raiz, hace30Dias);
    }

    private void auditarPostOrden(Nodo<T> nodo, LocalDateTime umbral) {
        if (nodo == null) return;

        // Post-orden: izquierda → derecha → raíz
        auditarPostOrden(nodo.izq, umbral);
        auditarPostOrden(nodo.der, umbral);

        boolean necesitaAuditoria =
                nodo.fechaUltimaAuditoria == null ||
                        nodo.fechaUltimaAuditoria.isBefore(umbral);

        if (necesitaAuditoria) {
            nodo.visitado = true;
            System.out.println("[AUDITORÍA] Depósito marcado: " + nodo.elemento);
        }
    }

    // ---------------------------------------------------------------
    // REPORTE POR NIVELES
    //
    // Imprime los nodos del nivel N (0 = raíz).
    // ---------------------------------------------------------------

    public void imprimirNivel(int nivel) {
        List<Nodo<T>> nodosEnNivel = new ArrayList<>();
        recolectarNivel(raiz, nivel, 0, nodosEnNivel);

        if (nodosEnNivel.isEmpty()) {
            System.out.println("Nivel " + nivel + ": (vacío)");
        } else {
            System.out.print("Nivel " + nivel + ": ");
            for (Nodo<T> n : nodosEnNivel) {
                System.out.print(n.elemento + "  ");
            }
            System.out.println();
        }
    }

    private void recolectarNivel(Nodo<T> nodo, int nivelBuscado,
                                 int nivelActual, List<Nodo<T>> lista) {
        if (nodo == null) return;
        if (nivelActual == nivelBuscado) {
            lista.add(nodo);
            return;
        }
        recolectarNivel(nodo.izq, nivelBuscado, nivelActual + 1, lista);
        recolectarNivel(nodo.der, nivelBuscado, nivelActual + 1, lista);
    }

    /** Imprime todos los niveles del árbol de corrido. */
    public void imprimirTodosLosNiveles() {
        int h = altura(raiz);
        for (int i = 0; i < h; i++) {
            imprimirNivel(i);
        }
    }

    // ---------------------------------------------------------------
    // Auxiliares
    // ---------------------------------------------------------------

    private int altura(Nodo<T> nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(altura(nodo.izq), altura(nodo.der));
    }

    // Ejercicios

    /*
    Contar cantidad de nodos.
    Contar cantidad de hojas.
    Obtener el valor máximo del árbol.
    Devolver el máximo de las hojas.
    Contar nodos cuyo valor está dentro de un rango [min, max].
    Devolver la longitud de la rama más corta desde la raíz a una hoja.
    Cambiar el nodo actual con el mayor valor de sus hijos, siempre que ambos no sean nulos.
     */

    // Contar cantidad de nodos.

    public int contarNodos(){
        return contadorNodos(raiz);
    }

    private int contadorNodos(Nodo<T> nodo) {
        if (nodo == null) return 0;
        return 1 + contadorNodos(nodo.izq) + contadorNodos(nodo.der);
    }

    // Contar cantidad de hojas

    public int contarHojas(){
        return contarHojas(raiz);
    }

    private int contarHojas(Nodo<T> nodo) {
        // Caso base
        if (nodo == null) return 0;
        if (nodo.izq == null && nodo.der == null) return 1;
        // Caso recursivo
        return contarHojas(nodo.der) + contarHojas(nodo.izq);
    }

    // Obtener el valor máximo del árbol

    public T obtenerMayor(){
        return obtenerMayor(raiz);
    }

    private T obtenerMayor(Nodo<T> nodo) {
        if (nodo == null) return null;
        if (nodo.der == null) return nodo.elemento;

        return obtenerMayor(nodo.der);
    }

    // Devolver el máximo de las hojas

    // Contar nodos cuyo valor está dentro de un rango [min, max].

    // Devolver la longitud de la rama más corta desde la raíz a una hoja.

    // Cambiar el nodo actual con el mayor valor de sus hijos, siempre que ambos no sean nulos.

}

