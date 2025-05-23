package server;

import java.io.*;
import java.net.Socket;

public class ClientHander implements Runnable {
    private Socket clientSocket;// customer?
    private BufferedReader input;
    private BufferedWriter output;
    private ChatServer chatServer;
    private String name; // id

    public ClientHander(Socket clientSocket, String name, ChatServer chatServer) {
        this.clientSocket = clientSocket;
        this.name = name;
        this.chatServer = chatServer;
        try {
            //create read and write
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) throws IOException {
        output.write(message + '\n');
        output.flush();
    }

    @Override
    public void run() {
        //when client name x chat, rest client will be received
        String message;
        while (true){
            try {
                if ((message = input.readLine()) != null){
                    chatServer.broadcastMessage(this.name, this.name + ": " + message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public String getName() {
        return name;
    }
}
