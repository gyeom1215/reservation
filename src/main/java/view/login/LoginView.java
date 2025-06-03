package view.login;

import com.google.gson.Gson;
import view.professor.ProfessorView;
import model.user.User;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import view.student.StudentView;
import client.ReservationClient;
import view.ta.TAView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginView extends JFrame {
  private JTextField idField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JButton signUpButton;
  private final ReservationClient reservationClient;
  private static final String USERS_FILE = "src/main/resources/users.json";
  private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

  public LoginView(ReservationClient reservationClient) {
    this.reservationClient = reservationClient;
    initializeUI();
  }

  private void initializeUI() {
    setTitle("로그인");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 300);
    setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // ID 입력
    gbc.gridx = 0;
    gbc.gridy = 0;
    mainPanel.add(new JLabel("아이디:"), gbc);

    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    idField = new JTextField(20);
    mainPanel.add(idField, gbc);

    // 비밀번호 입력
    gbc.gridx = 0;
    gbc.gridy = 1;
    mainPanel.add(new JLabel("비밀번호:"), gbc);

    gbc.gridx = 1;
    passwordField = new JPasswordField(20);
    mainPanel.add(passwordField, gbc);

    // 버튼 패널
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    loginButton = new JButton("로그인");
    signUpButton = new JButton("회원가입");

    loginButton.addActionListener(e -> handleLogin());
    signUpButton.addActionListener(e -> openSignUp());

    buttonPanel.add(loginButton);
    buttonPanel.add(signUpButton);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    mainPanel.add(buttonPanel, gbc);

    add(mainPanel);
  }

  private void handleLogin() {
    String id = idField.getText();
    String password = new String(passwordField.getPassword());

    if (id.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(this,
        "아이디와 비밀번호를 모두 입력해주세요.",
        "입력 오류",
        JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      // ReservationClient를 통해 로그인 시도
      LOGGER.info("로그인 시도 - ID: " + id);
      List<User> users = reservationClient.getAllUsers();
      LOGGER.info("조회된 사용자 수: " + users.size());
      
      User user = users.stream()
        .filter(u -> {
          boolean matches = u.getId().equals(id) && u.getPassword().equals(password);
          LOGGER.info("사용자 비교 - ID: " + u.getId() + ", 비밀번호 일치: " + matches);
          return matches;
        })
        .findFirst()
        .orElse(null);

      if (user != null) {
        LOGGER.info("로그인 성공 - 사용자: " + user.getName() + ", 역할: " + user.getRole());
        JOptionPane.showMessageDialog(this,
          "로그인 성공! " + user.getName() + "님 환영합니다.",
          "로그인 성공",
          JOptionPane.INFORMATION_MESSAGE);

        switch (user.getRole().toLowerCase()) {
          case "student" -> {
            StudentView studentView = new StudentView(user, reservationClient);
            studentView.setVisible(true);
          }
          case "professor" -> {
            ProfessorView professorView = new ProfessorView(user, reservationClient);
            professorView.setVisible(true);
          }
          case "ta" -> {
            TAView taView = new TAView(user, reservationClient);
            taView.setVisible(true);
          }
          default -> {
            LOGGER.warning("알 수 없는 사용자 역할: " + user.getRole());
            JOptionPane.showMessageDialog(this,
              "알 수 없는 사용자 역할입니다.",
              "오류",
              JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        dispose();
      } else {
        LOGGER.warning("로그인 실패 - ID: " + id);
        JOptionPane.showMessageDialog(this,
          "아이디 또는 비밀번호가 올바르지 않습니다.",
          "로그인 실패",
          JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error during login: " + e.getMessage(), e);
      JOptionPane.showMessageDialog(this,
        "로그인 처리 중 오류가 발생했습니다: " + e.getMessage(),
        "오류",
        JOptionPane.ERROR_MESSAGE);
    }
  }

  private void openSignUp() {
    dispose();
    new SignUpView(reservationClient).setVisible(true);
  }

  private List<User> loadUsers() throws IOException {
    List<User> users = new ArrayList<>();
    File file = new File(USERS_FILE);
    
    if (!file.exists()) {
      return users;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      StringBuilder jsonContent = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        jsonContent.append(line);
      }

      if (jsonContent.length() == 0) {
        return users;
      }

      JSONArray jsonArray = new JSONArray(jsonContent.toString());
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject obj = jsonArray.getJSONObject(i);
        User user = new User(
          obj.getString("id"),
          obj.getString("password"),
          obj.getString("name"),
          obj.getString("department"),
          obj.getString("role")
        );
        users.add(user);
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error loading users: " + e.getMessage(), e);
      throw e;
    }
    return users;
  }

  // 서버 응답을 파싱하기 위한 내부 클래스
  private static class ServerResponse {
    String dept;
    public String name;
    String result;
    String role;
  }
}