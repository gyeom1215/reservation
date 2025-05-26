package com.deu.reservation.client;

import com.deu.reservation.client.view.login.LoginView;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMain {
    private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Look and Feel 설정 중 오류 발생: " + e.getMessage(), e);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("클라이언트 초기화 중...");
                ReservationClient reservationClient = new ReservationClient();
                System.out.println("클라이언트 초기화 완료");

                System.out.println("로그인 화면 시작 중...");
                LoginView loginView = new LoginView(reservationClient);
                loginView.setVisible(true);
                System.out.println("로그인 화면 시작 완료");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "애플리케이션 시작 중 오류 발생: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(null,
                    "애플리케이션을 시작하는 중 오류가 발생했습니다: " + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 