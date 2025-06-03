package server;

import client.ReservationClient;
import server.ReservationServer;
import view.login.LoginView;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// 메인 애플리케이션 클래스
public class MainApp {
    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        try {
            System.out.println("서버 초기화 중...");
            ReservationServer reservationServer = new ReservationServer();
            System.out.println("서버 초기화 완료");

            System.out.println("클라이언트 초기화 중...");
            ReservationClient reservationClient = ReservationClient.getInstance(reservationServer);
            System.out.println("클라이언트 초기화 완료");

            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("로그인 화면 시작 중...");
                    LoginView loginView = new LoginView(reservationClient);
                    loginView.setVisible(true);
                    System.out.println("로그인 화면 시작 완료");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error starting login screen: " + e.getMessage(), e);
                    System.err.println("로그인 화면 시작 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "로그인 화면을 시작하는 중 오류가 발생했습니다: " + e.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing application: " + e.getMessage(), e);
            System.err.println("애플리케이션 초기화 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "애플리케이션을 시작하는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 