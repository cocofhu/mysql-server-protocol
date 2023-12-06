package com.cocofhu;

import com.cocofhu.server.mysql.NativeServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(9988)) {

            Socket socket = server.accept();
            NativeServer nativeServer = new NativeServer(socket, 1);
            Thread.sleep(111111);
            System.out.println(nativeServer);
        }
    }
}