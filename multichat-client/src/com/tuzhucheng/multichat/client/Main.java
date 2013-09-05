package com.tuzhucheng.multichat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

    Socket clientSocket;
    PrintWriter out;
    BufferedReader userInput;
    ClientReceiver clientReceiver;

    private static final int SERVER_PORT = 4545;

    String line;

    public static void main(String[] args) {
	    Main uiSender = new Main();
        uiSender.go();
    }

    private void go() {
        try {
            clientSocket = new Socket("localhost", SERVER_PORT);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientReceiver = new ClientReceiver(clientSocket);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            clientReceiver.start();

            while(true) {
                line = userInput.readLine();
                out.println(line);
                if(line.equals("END")) {
                    clientReceiver.interrupt();
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Caught IOException");
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                System.err.println("Client socket failed to close.");
            }
        }

        System.out.println("Main thread ended");
    }
}
