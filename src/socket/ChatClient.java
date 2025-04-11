package socket;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void listen(Consumer<String> onMessageReceived) {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    onMessageReceived.accept(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
