package com.deu.reservation.client.util;

import com.deu.reservation.client.protocol.Message;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkUtil {
    private static final Logger LOGGER = Logger.getLogger(NetworkUtil.class.getName());
    private static final Gson gson = new Gson();
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static Socket connectToServer() throws IOException {
        try {
            return new Socket(SERVER_HOST, SERVER_PORT);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "서버 연결 중 오류 발생: " + e.getMessage(), e);
            throw e;
        }
    }

    public static void sendMessage(Socket socket, Message message) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String json = gson.toJson(message);
        out.println(json);
    }

    public static Message receiveMessage(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String json = in.readLine();
        if (json == null) {
            throw new IOException("연결이 종료되었습니다.");
        }
        return gson.fromJson(json, Message.class);
    }
} 