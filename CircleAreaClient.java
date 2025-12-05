import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CircleAreaClient extends JFrame {

    // Input field for the radius
    private JTextField radiusField = new JTextField();

    // Area to display conversation with the server
    private JTextArea displayArea = new JTextArea();

    // Network streams
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public static void main(String[] args) {
        new CircleAreaClient();
    }

    public CircleAreaClient() {
        // Build GUI
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Enter radius: "), BorderLayout.WEST);
        topPanel.add(radiusField, BorderLayout.CENTER);
        radiusField.setHorizontalAlignment(JTextField.RIGHT);

        setTitle("Circle Area Client");
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Try to connect to the server
        connectToServer();

        // When user presses Enter in the text field
        radiusField.addActionListener(new RadiusListener());
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 8000);

            // Set up IO streams
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());

            displayArea.append("Connected to server on port 8000.\n");
        } catch (IOException ex) {
            displayArea.append("Failed to connect to server: " + ex.getMessage() + "\n");
        }
    }

    // Handles user pressing Enter after typing a radius
    private class RadiusListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (toServer == null || fromServer == null) {
                JOptionPane.showMessageDialog(
                        CircleAreaClient.this,
                        "Not connected to the server.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String text = radiusField.getText().trim();
            if (text.isEmpty()) {
                return;
            }

            try {
                double radius = Double.parseDouble(text);

                // Send the radius to the server
                toServer.writeDouble(radius);
                toServer.flush();

                // Receive the area from the server
                double area = fromServer.readDouble();

                // Show the results in the text area
                displayArea.append("Radius: " + radius + "\n");
                displayArea.append("Area received from server: " + area + "\n\n");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        CircleAreaClient.this,
                        "Please enter a valid number for the radius.",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE
                );
            } catch (IOException ex) {
                displayArea.append("Communication error: " + ex.getMessage() + "\n");
            }
        }
    }
}
