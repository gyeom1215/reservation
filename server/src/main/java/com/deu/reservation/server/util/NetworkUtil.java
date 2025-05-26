package com.deu.reservation.server.util;

import com.deu.reservation.server.protocol.Message;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkUtil {
    private static final Logger LOGGER = Logger.getLogger(NetworkUtil.class.getName());
    private static final Gson gson = new Gson();

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