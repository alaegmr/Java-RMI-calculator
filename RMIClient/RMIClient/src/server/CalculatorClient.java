package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Calculator;

public class CalculatorClient extends JFrame {
    private Calculator calculator;
    private JTextField numField1;
    private JTextField numField2;
    private JComboBox<String> operationComboBox;
    private JTextField resultField;

    public CalculatorClient() {
        try {
            // Look up the RMI registry
            calculator = (Calculator) Naming.lookup("rmi://localhost:1099/CalculatorService");

            // Create GUI components
            numField1 = new JTextField(10);
            numField2 = new JTextField(10);
            operationComboBox = new JComboBox<>(new String[]{"+", "-", "*", "/"});
            resultField = new JTextField(10);
            resultField.setEditable(false);

            JButton calculateButton = new JButton("Calculate");

            // Add action listener to the button
            calculateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        calculate();
                    } catch (RemoteException ex) {
                        Logger.getLogger(CalculatorClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            // Create layout using JPanel
            JPanel panel = new JPanel();
            panel.add(new JLabel("Number 1:"));
            panel.add(numField1);
            panel.add(new JLabel("Number 2:"));
            panel.add(numField2);
            panel.add(new JLabel("Operation:"));
            panel.add(operationComboBox);
            panel.add(calculateButton);
            panel.add(new JLabel("Result:"));
            panel.add(resultField);

            // Set up the JFrame
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("RMI Calculator");
            this.setSize(300, 200);
            this.setContentPane(panel);
            this.setVisible(true);
        } catch (Exception e) {
            System.err.println("Calculator client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private void calculate() throws RemoteException {
        try {
            // Get user input
            double a = Double.parseDouble(numField1.getText());
            double b = Double.parseDouble(numField2.getText());
            String operation = (String) operationComboBox.getSelectedItem();

            // Perform the selected operation
            double result;
            switch (operation) {
                case "+":
                    result = calculator.add(a, b);
                    break;
                case "-":
                    result = calculator.subtract(a, b);
                    break;
                case "*":
                    result = calculator.multiply(a, b);
                    break;
                case "/":
                    result = calculator.divide(a, b);
                    break;
                default:
                    result = 0;
                    break;
            }

            // Display the result
            resultField.setText(String.valueOf(result));
        } catch (NumberFormatException | RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorClient());
    }
}
