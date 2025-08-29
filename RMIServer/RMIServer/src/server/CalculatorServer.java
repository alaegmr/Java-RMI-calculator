package server;



import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            // Create and start the RMI registry on port 1099
            LocateRegistry.createRegistry(1099);

            Calculator calculator = new CalculatorImpl();
            // Bind the calculator object to the registry
            Naming.rebind("CalculatorService", calculator);

            System.out.println("Calculator server is running...");
        } catch (Exception e) {
            System.err.println("Calculator server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

