package com.company;

import protocol.Message;
import protocol.Protocol;
import sun.misc.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        final List<Socket> clients = new ArrayList<Socket>();

        try {
//            ServerSocket serverSocket = new ServerSocket(10000, 0, InetAddress.getByName("0.0.0.0"));
            ServerSocket serverSocket = new ServerSocket(10000);
            while (true) {

                final Socket s = serverSocket.accept();
                clients.add(s);
                System.out.println("clients=" + clients.size());

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            InputStream iStream = s.getInputStream();
                            OutputStream oStream = s.getOutputStream();

                            try {
                                while (!s.isClosed()) {

                                    Message message = Protocol.deserialize(iStream);

                                    System.out.println("Пришло: " + message.getText());

                                    for (Socket client : clients) {
                                        client.getOutputStream().write(Protocol.serialize(message));
                                    }
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            iStream.close();
                            oStream.close();
                            s.close();
                            clients.remove(s);
                            System.out.println("clients=" + clients.size());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
