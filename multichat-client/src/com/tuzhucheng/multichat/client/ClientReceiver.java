package com.tuzhucheng.multichat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * A class that receives messages from the server and prints them to the screen
 */
public class ClientReceiver extends Thread {

    private Socket clientSocket;
    private Main mainHandle;
    private BufferedReader socketIn;
    private String line;

    public ClientReceiver(Main mainHandle, Socket clientSocket) {
        this.mainHandle = mainHandle;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                line = socketIn.readLine();
                if (line != null) {
                    if (line.equals("END")) {
                        System.out.println("Server sent END message.");
                        clientSocket.shutdownOutput();
                        System.out.println("Shutted down socket output");
                    } else if (line.equals("CLOSE_SOCKET_INPUT")) {
                        clientSocket.shutdownInput();
                        System.out.println("Shutted down socket input");
                        break;
                    } else
                        System.out.println(line);

                } else {
                    System.out.println("Server sent null. Exiting...");
                    break;
                }
            }
        } catch (SocketException e) {
            // Typically this means that the server socket has been closed. Hence the client should close
            // its socket as well and shut down.
            System.out.println("Caught SocketException");
        } catch (IOException e) {
            System.out.println("Caught IOException");
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Closed client socket");
            } catch (IOException e) {
                System.err.println("BufferedReader or socket failed to close.");
            }

            System.out.println("ClientReceiver thread ended.");
            System.out.println("Server has shut down. Press any key to quit.");
        }

    }
}
