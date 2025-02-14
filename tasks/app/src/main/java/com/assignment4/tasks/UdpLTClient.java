package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UdpLTClient {
    public static void main(String[] args) throws Exception
    {
        // ask for the process id between 1 and 3 as 0 is allocated to the server
        System.out.println("Enter your id (1 to 3): ");
        Scanner id_input = new Scanner(System.in);
        int id = id_input.nextInt();
        int port = 4040;

        // prepare the client socket
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // initialize the buffers
        byte[] sendData;

        // initialize the clock
        int startTime = 0;
        LamportTimestamp lc = new LamportTimestamp(startTime);

        //ask for user input aka message to the server
        System.out.println("Enter any message "+id+":");
        Scanner input = new Scanner(System.in);


        while(true) {

                String messageBody = input.nextLine();
                int messageTime = lc.getCurrentTimestamp();
                String responseMessage = messageBody + ':' + messageTime;

                /*
                 * write your code to increment clock when the message is sent
                 */
                lc.tick();

                // check if the user wants to quit
                if(messageBody.equalsIgnoreCase("quit")){
                    clientSocket.close();
                    System.exit(1);
                }

                /*
                 * write your code to send the message to the server
                 */

                // Convert the message to bytes
                sendData = responseMessage.getBytes();

                // create a DatagramPacket to send the message
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

                // send the packet
                clientSocket.send(sendPacket);
                
                // Create and start client thread for receiving messages
                LTClientThread client;
                client = new LTClientThread(clientSocket, lc);
                Thread receiverThread = new Thread(client);
                receiverThread.start();

        }

    }
}
