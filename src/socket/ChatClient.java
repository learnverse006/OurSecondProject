// File: socket/ChatClient.java
package socket;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dataOut = new DataOutputStream(socket.getOutputStream());
        dataIn = new DataInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getWriter() {
        return out;
    }

    public DataInputStream getDataInputStream() {
        return dataIn;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOut;
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

    public void close() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
