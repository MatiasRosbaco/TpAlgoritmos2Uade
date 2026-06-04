package TDAs;

import java.time.LocalDateTime;

public class Nodo<V> {
    V elemento;
    boolean visitado;
    LocalDateTime fechaUltimaAuditoria;

    Nodo(V elemento) {
        this.elemento = elemento;
        this.visitado = false;
        this.fechaUltimaAuditoria = null;
    }

    Nodo<V> izq;
    Nodo<V> der;

    @Override
    public String toString() {
        return elemento.toString();
    }
}