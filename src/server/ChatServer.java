package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final int PORT = 8080;
    private List<ClientHander> clients = new ArrayList<>();


    //start server
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("server started");
            //wait client connects
            while (true) {
                Socket clientSocket = serverSocket.accept();// tao mot ket noi giua khach hang va server
                ClientHander clientHander = new ClientHander(clientSocket, (char) ('A'+ clients.size()) + "", this);
                clients.add(clientHander);
                System.out.println("client " + clients.size() +  " connected!");
                new Thread(clientHander).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //load rest client
    public void broadcastMessage(String name, String message) throws IOException {
        for (ClientHander client : clients) {
            if (!client.getName().equals(name)) {
                client.sendMessage(message);
            }
        }
    }
}
