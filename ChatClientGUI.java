import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClientGUI {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Chat Client");
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());

        frame.setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Unable to connect to server", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        if (out != null) {
            String message = textField.getText();
            if (!message.isEmpty()) {
                out.println(message);
                textField.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Not connected to server", "Send Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> {
                    textArea.append(finalMessage + "\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
