import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CircleAreaServer extends JFrame {

    // Text area to show server activity
    private JTextArea logArea = new JTextArea();

    public static void main(String[] args) {
        new CircleAreaServer();
    }

    public CircleAreaServer() {
        // Basic GUI setup
        setTitle("Circle Area Server");
        setLayout(new BorderLayout());
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Start the server logic in a separate thread
        new Thread(() -> startServer()).start();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            logArea.append("Server started at " + new Date() + "\n");
            logArea.append("Waiting for a client to connect...\n");

            // Wait for a single client connection
            Socket socket = serverSocket.accept();
            logArea.append("Client connected: " + socket.getInetAddress() + "\n");

            // Streams for communication with the client
            DataInputStream inputFromClient =
                    new DataInputStream(socket.getInputStream());
            DataOutputStream outputToClient =
                    new DataOutputStream(socket.getOutputStream());

            // Repeatedly read a radius, compute area, and send result
            while (true) {
                double radius = inputFromClient.readDouble();
                double area = Math.PI * radius * radius;

                // Send result back to the client
                outputToClient.writeDouble(area);
                outputToClient.flush();

                // Log what happened on the server window
                logArea.append("Radius received from client: " + radius + "\n");
                logArea.append("Area sent to client: " + area + "\n");
            }
        } catch (IOException ex) {
            logArea.append("Server error: " + ex.getMessage() + "\n");
        }
    }
}
