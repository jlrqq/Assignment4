package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.*;


public class UdpVectorClient {

    public static void main(String[] args) throws Exception
    {
        System.out.println("Enter your id (1 to 3): ");
        Scanner id_input = new Scanner(System.in);
        int id = id_input.nextInt();

        // prepare the client socket
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // initialize the buffers
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        int port = 4040;
        List<String> logs;

        int startTime = 0;
        VectorClock vcl = new VectorClock(4);
        vcl.setVectorClock(id, startTime);

        //ask for user input aka message to the server
        System.out.println(id+": Enter any message:");
        Scanner input = new Scanner(System.in);

        while(true) {
            String messageBody = input.nextLine();
            // increment clock
            if (!messageBody.isEmpty()){
                vcl.tick(id);
            }
            HashMap<Integer, Integer> messageTime = new HashMap<>();
            messageTime.put(id,vcl.getCurrentTimestamp(id));
            Message msg = new Message(messageBody, messageTime);
            String responseMessage = msg.content + ':' + msg.messageTime;

            // check if the user wants to quit
            if(messageBody.equals("quit")){
                clientSocket.close();
                System.exit(1);
            }

            // send the message to the server
            sendData = responseMessage.getBytes();

            /*
             * write your code to send message to the server. clientSocket.send(messageTosend);
             */

            DatagramPacket messageTosend = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(messageTosend);

            // check if the user wants to see the history
            if(messageBody.equals("history")) {
                System.out.println("Receiving the chat history...");
                logs = new ArrayList<>();

                /*
                 * write your code to receive the logs, clientSocket.receive(getack);
                 * it should keep receiving till all the messages are reached.
                 * You can use the clientSocket.setSoTimeout(timeinmiliseconds); to detect if the all the messages have been received
                 * update the logs list
                 */
                clientSocket.setSoTimeout(50000);
                while (true) { 
                    try {
                        DatagramPacket messageToreceive = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(messageToreceive);
                        messageToreceive = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(messageToreceive);
                        String receivedMessage = new String(messageToreceive.getData(), 0, messageToreceive.getLength());
                        if (!receivedMessage.isEmpty()) {
                            logs.add(receivedMessage);
                        } else
                            break;
                    } catch (SocketTimeoutException e) {
                        System.out.println("Error timeout reached.");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                UdpVectorClient uc = new UdpVectorClient();
                uc.showHistory(logs); // gives out all the unsorted logs stored at the server
                uc.showSortedHistory(logs); // shows sorted logs

                System.out.println("History successfully printed!");
            }
            else
            {
                VectorClientThread client;
                client = new VectorClientThread(clientSocket, vcl, id);
                Thread receiverThread = new Thread(client);
                receiverThread.start();
            }
        }
    }
    public void showHistory(List<String> logs){

        // prints the unsorted logs (history) coming form the server
        for (String message : logs) {

            System.out.println(message);
        }
    }
    public void showSortedHistory(List<String> logs){

        // prints sorted logs (history) received
        System.out.println("Print sorted conversation using attached vector clocks");
        Map<int[], String> logMap = new HashMap<>();

        /*
         * write your code to sort the logs (history) in ascending order
         * to sort the logs, use the clock array, for example, [0,0,1,1] as key the to the logMap.
         * Since this is a custom sorting, create a custom comparator to sort logs
         * once sorted print the logs that are following the correct sequence of the message flow
         * to store the sorted logs for printing you could use LinkedHashMap
         */

        // Create a custom comparator to sort logs
        Comparator<String> customComparator = new Comparator<String>() {
            @Override
            public int compare(String message1, String message2) {
                // Extract the vector clocks from the messages
                int[] clock1 = extractVectorClock(message1);
                int[] clock2 = extractVectorClock(message2);

                // Compare the vector clocks individually 
                for (int i = 0; i < clock1.length; i++) {
                    if (clock1[i] < clock2[i]) {
                        return -1;
                    } else if (clock1[i] > clock2[i]) {
                        return 1;
                    }
                }

                // If vector clocks are equal, compare the entire message
                return message1.compareTo(message2);
        }
        
            // Extract and convert the vector clock from the message into an array
            private int[] extractVectorClock(String message) {
                String clockString = message.substring(message.lastIndexOf('[') + 1, message.lastIndexOf(']'));
                String[] clockValues = clockString.split(", ");
                int[] clock = new int[clockValues.length];
                for (int i = 0; i < clock.length; i++) {
                    clock[i] = Integer.parseInt(clockValues[i]);
                }
                return clock;
            }
        };

        // Sort the logs using the custom comparator
        Collections.sort(logs, customComparator);

        // Print the sorted logs
        for (String message : logs) {
            System.out.println(message);
        }
    }
}
