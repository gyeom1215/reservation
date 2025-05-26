package com.deu.reservation.client.view.login;

import com.deu.reservation.client.ReservationClient;
import com.deu.reservation.client.model.User;
import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    private final ReservationClient client;
    private JTextField idField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox;

    public RegisterView(ReservationClient client) {
        this.client = client;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("아이디:"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("비밀번호:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("이름:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("이메일:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("전화번호:"));
        phoneField = new JTextField();
        panel.add(phoneField);

        panel.add(new JLabel("역할:"));
        roleComboBox = new JComboBox<>(new String[]{"학생", "교수", "TA 조교"});
        panel.add(roleComboBox);

        JButton registerButton = new JButton("회원가입");
        registerButton.addActionListener(e -> handleRegister());
        panel.add(registerButton);

        add(panel);
    }

    private void handleRegister() {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String role = getRoleFromComboBox();

        User user = new User(id, password, name, email, phone);
        boolean success = client.register(user);
        if (success) {
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "회원가입에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getRoleFromComboBox() {
        String selectedRole = (String) roleComboBox.getSelectedItem();
        switch (selectedRole) {
            case "교수":
                return User.ROLE_PROFESSOR;
            case "TA 조교":
                return User.ROLE_TA;
            default:
                return User.ROLE_STUDENT;
        }
    }
} 