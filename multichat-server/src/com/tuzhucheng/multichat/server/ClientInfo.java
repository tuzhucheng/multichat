package com.tuzhucheng.multichat.server;

public class ClientInfo {
    private ClientCommunicator communicator;
    private int port;
    private String status;

    public static final String STATE_CLOSED_INPUTSTREAM = "state_closed_inputstream";
    public static final String STATE_CLOSED_OUTPUTSTREAM = "state_closed_outputstream";

    public ClientInfo(ClientCommunicator communicator) {
        this.communicator = communicator;
        port = communicator.getSocket().getPort();
    }

    synchronized public void setStatus(String status) {
        this.status = status;
        notifyAll();
    }

    public int getPort() {
        return port;
    }

    synchronized public String getStatus() {
        return status;
    }

    public ClientCommunicator getCommunicator() {
        return communicator;
    }

}
