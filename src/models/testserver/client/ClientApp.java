package models.testserver.client;

import models.testserver.server.ChatServer;

public class ClientApp {
    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.startClient();
    }
}
