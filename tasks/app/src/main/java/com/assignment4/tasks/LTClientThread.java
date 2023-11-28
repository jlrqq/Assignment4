package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LTClientThread implements Runnable {

    private final DatagramSocket clientSocket;
    LamportTimestamp lc;

    byte[] receiveData = new byte[1024];

    public LTClientThread(DatagramSocket clientSocket, LamportTimestamp lc) {
        this.clientSocket = clientSocket;
        this.lc = lc;
    }

    @Override
    public void run() {
        try {
            while (true) {
                 /*
                  * write your code to continuously receive the response from the server and update the clock value with the received value
                  */
                // Continuously receive the response from the server
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                /*
                    * write your code to parse the response. Remember the response you receive is in message:timestamp format.
                    * response.split(":");
                    * update clock every time the client receives a message
                */
                // Parse the response 
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String[] parts = response.split(":");
                String message = parts[0];
                int serverTimestamp = Integer.parseInt(parts[1]);

                // Update the clock value with the received timestamp
                lc.updateClock(serverTimestamp);

                // Print the received message and updated timestamp
                System.out.println("Server: " + message + ":" + lc.getCurrentTimestamp());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
