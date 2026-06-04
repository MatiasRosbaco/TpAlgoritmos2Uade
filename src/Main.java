import TDAs.ABB;
import model.Deposito;

public class Main {

    public static void main(String[] args) {

        ABB<Deposito> arbol = new ABB<>();

        // Agregar depósitos
        arbol.agregar(new Deposito(10));
        arbol.agregar(new Deposito(5));
        arbol.agregar(new Deposito(15));
        arbol.agregar(new Deposito(3));
        arbol.agregar(new Deposito(7));
        arbol.agregar(new Deposito(20));

        // Reporte por niveles
        System.out.println("=== REPORTE POR NIVELES ===");
        arbol.imprimirTodosLosNiveles();

        // Auditoría: marca los depósitos no auditados en los últimos 30 días
        // (todos, porque fechaUltimaAuditoria es null en todos)
        System.out.println("\n=== AUDITORÍA ===");
        arbol.auditarPendientes();
    }
}