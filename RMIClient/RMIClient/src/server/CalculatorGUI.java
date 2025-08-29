package server;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Calculator;
import server.CalculatorClient;

public class CalculatorGUI extends JFrame {
    private Calculator calculator;
    private JTextField displayField;

    private String currentInput;
    private String currentOperation;
    private boolean newInput;

    public CalculatorGUI() {
        try {
            // Look up the RMI registry
            calculator = (Calculator) Naming.lookup("rmi://localhost:1099/CalculatorService");

            // Create display field
            displayField = new JTextField();
            displayField.setEditable(false);
            displayField.setHorizontalAlignment(JTextField.RIGHT);

            // Initialize variables
            currentInput = "";
            currentOperation = "";
            newInput = true;

            // Create number buttons
            String[] numberButtons = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0"};
            JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));

            for (String number : numberButtons) {
                JButton button = new JButton(number);
                button.addActionListener(new NumberButtonListener());
                buttonPanel.add(button);
            }

            // Create operation buttons
            String[] operationButtons = {"/", "*", "-", "+"};
            for (String operation : operationButtons) {
                JButton button = new JButton(operation);
                button.addActionListener(new OperationButtonListener(operation));
                buttonPanel.add(button);
            }

            // Create equals button
            JButton equalsButton = new JButton("=");
            equalsButton.addActionListener(new EqualsButtonListener());
            buttonPanel.add(equalsButton);

            // Create clear button
            JButton clearButton = new JButton("C");
            clearButton.addActionListener(new ClearButtonListener());
            buttonPanel.add(clearButton);

            // Create layout using JPanel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(displayField, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.CENTER);

            // Set up the JFrame
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("RMI Calculator");
            this.setSize(300, 400);
            this.setContentPane(mainPanel);
            this.setVisible(true);
        } catch (Exception e) {
            System.err.println("Calculator GUI exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private class NumberButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            appendToDisplay(button.getText());
        }
    }

 private class OperationButtonListener implements ActionListener {
    private String operation;

    public OperationButtonListener(String operation) {
        this.operation = operation;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentInput.isEmpty()) {
            currentInput = displayField.getText();
        }

        if (!newInput) {
            // Perform the operation only if there is a current input
            performOperation(operation);
            newInput = true;
        }
    }
}


private class EqualsButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Set currentInput before calculating result
        currentInput = displayField.getText();
        calculateResult();
    }
}

    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearDisplay();
        }
    }

    private void appendToDisplay(String input) {
        if (newInput) {
            displayField.setText(input);
            newInput = false;
        } else {
            displayField.setText(displayField.getText() + input);
        }
    }

 private void performOperation(String operation) {
    try {
        if (!newInput) {
            // If a new input is flagged, use the displayed value as the first number
            if (currentInput.isEmpty()) {
                currentInput = displayField.getText();
            }
            currentOperation = operation;
            newInput = true;
        }
    } catch (NumberFormatException ex) {
        displayField.setText("Error");
        ex.printStackTrace();
    }
}

private void calculateResult() {
    try {
        if (!currentInput.isEmpty()) {
            double num1 = Double.parseDouble(currentInput);
            double num2 = Double.parseDouble(displayField.getText());
            double result;

            switch (currentOperation) {
                case "+":
                    result = calculator.add(num1, num2);
                    break;
                case "-":
                    result = calculator.subtract(num1, num2);
                    break;
                case "*":
                    result = calculator.multiply(num1, num2);
                    break;
                case "/":
                    if (num2 != 0) {
                        // Perform division only if the divisor is not zero
                        result = calculator.divide(num1, num2);
                    } else {
                        // Handle division by zero
                        displayField.setText("Error: Division by zero");
                        return;
                    }
                    break;
                default:
                    result = 0;
                    break;
            }

            displayField.setText(String.valueOf(result));
        } else {
            displayField.setText("Error: Invalid input");
        }
    } catch (NumberFormatException | RemoteException ex) {
        displayField.setText("Error: Invalid input");
        ex.printStackTrace();
    }
}


    private void clearDisplay() {
        displayField.setText("");
        currentInput = "";
        currentOperation = "";
        newInput = true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorGUI());
    }
}
