package com.deu.reservation.client.view.login;

import com.deu.reservation.client.ReservationClient;
import com.deu.reservation.client.model.User;
import com.deu.reservation.client.service.ReservationService;
import com.deu.reservation.client.view.StudentMainView;
import com.deu.reservation.client.view.ProfessorMainView;
import com.deu.reservation.client.view.TAMainView;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private final ReservationClient client;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginView(ReservationClient client) {
        this.client = client;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("강의실 예약 시스템 - 로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("아이디:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("비밀번호:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton);

        JButton registerButton = new JButton("회원가입");
        registerButton.addActionListener(e -> {
            RegisterView registerView = new RegisterView(client);
            registerView.setVisible(true);
        });
        panel.add(registerButton);

        add(panel);
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean loginSuccess = client.login(username, password);
            if (loginSuccess) {
                User user = client.getCurrentUser();
                if (user != null) {
                    ReservationService reservationService = new ReservationService();
                    JFrame mainView;
                    
                    if (user.isStudent()) {
                        mainView = new StudentMainView(user, reservationService, client);
                    } else if (user.isProfessor()) {
                        mainView = new ProfessorMainView(user, reservationService, client);
                    } else if (user.isTA()) {
                        mainView = new TAMainView(user, reservationService, client);
                    } else {
                        JOptionPane.showMessageDialog(this, "알 수 없는 사용자 역할입니다.", "오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    mainView.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "사용자 정보를 가져오는데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "로그인 처리 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
} 