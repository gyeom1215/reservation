package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class ServerMain {
  private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

  public static void main(String[] args) {
    // 서버 실행 시 로그인(MainApp) UI도 동시에 실행
    SwingUtilities.invokeLater(() -> {
      MainApp.main(new String[0]);
    });

    final int PORT = 12345;
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("서버가 " + PORT + " 포트에서 시작되었습니다.");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("새 클라이언트 접속: " + clientSocket.getInetAddress());
        new Thread(new ClientHandler(clientSocket)).start();
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error starting server: " + e.getMessage(), e);
      System.err.println("서버 시작 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }
}