package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    public void startClient(){
        try {
            Socket clientSocket = new Socket(HOST, PORT);
            System.out.println("Connected successfully!");
            ClientListener clientListener = new ClientListener(clientSocket);
            new Thread(clientListener).start();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));//
            Scanner sc = new Scanner(System.in);
            while (true){
                String message = sc.nextLine();
                writer.write(message + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
