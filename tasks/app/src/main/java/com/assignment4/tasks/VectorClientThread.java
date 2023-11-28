package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class VectorClientThread implements Runnable {

    private final DatagramSocket clientSocket;
    VectorClock vcl;
    byte[] receiveData = new byte[1024];

    int id;
    public VectorClientThread(DatagramSocket clientSocket, VectorClock vcl, int id) {

        this.clientSocket = clientSocket;
        this.vcl = vcl;
        this.id = id;
    }

    @Override
    public void run() {
        String response = ""; //update this with the real response string from server
        /*
         * Write your code to receive messgaes from the server and update the vector clock
         */
        try {
            while (true) {
                // Receive the message from the server
                DatagramPacket messageToreceive = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(messageToreceive);
                response = new String(messageToreceive.getData(), 0, messageToreceive.getLength());

                /*
                    * you could use "replaceAll("\\p{Punct}", " ").trim().split("\\s+");" for filteing the received message timestamps
                    * update clock and increament local clock (tick) for receiving the message
                */
                // Filter the received message timestamps
                // System.out.println(response);
                String[] responseMessageArray = response.split(":");
                
                if (responseMessageArray.length > 1 && !responseMessageArray[1].contains("[")) {
                    String[] timestamps = response.replaceAll("\\p{Punct}", " ").trim().split("\\s+");
                   
                    // Create a new VectorClock to represent the received clock
                    VectorClock receivedClock = new VectorClock(4);

                    // Update the received clock based on the received timestamps
                    receivedClock.setVectorClock(0, Integer.parseInt(timestamps[timestamps.length-1]));

                    // Update the local vector clock using the received clock
                    vcl.updateClock(receivedClock);

                    // Increment local clock for receiving the message
                    vcl.tick(id);
                }
                
                // Print the received message and updated vector clock
                System.out.println("Server: " + responseMessageArray[0] + " " + vcl.showClock());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
}
