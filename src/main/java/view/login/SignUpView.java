package view.login;

import client.ReservationClient;
import model.user.User;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUpView extends JFrame {
  private JTextField tfId, tfName, tfDepartment;
  private JPasswordField pfPassword;
  private JComboBox<String> cbRole;
  private JButton btnSignUp, btnCancel;
  private final ReservationClient reservationClient;
  private static final Logger LOGGER = Logger.getLogger(SignUpView.class.getName());

  public SignUpView(ReservationClient reservationClient) {
    this.reservationClient = reservationClient;
    setTitle("회원가입");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(400, 300);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(6, 2, 10, 10));

    add(new JLabel("아이디:"));
    tfId = new JTextField();
    add(tfId);

    add(new JLabel("비밀번호:"));
    pfPassword = new JPasswordField();
    add(pfPassword);

    add(new JLabel("이름:"));
    tfName = new JTextField();
    add(tfName);

    add(new JLabel("학과:"));
    tfDepartment = new JTextField();
    add(tfDepartment);

    add(new JLabel("역할:"));
    cbRole = new JComboBox<>(new String[]{"학생", "교수", "조교"});
    add(cbRole);

    btnSignUp = new JButton("회원가입");
    btnCancel = new JButton("취소");
    add(btnSignUp);
    add(btnCancel);

    btnSignUp.addActionListener(e -> {
      String id = tfId.getText().trim();
      String pw = new String(pfPassword.getPassword()).trim();
      String name = tfName.getText().trim();
      String dept = tfDepartment.getText().trim();
      String role = (String) cbRole.getSelectedItem();

      if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || dept.isEmpty()) {
        JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
        return;
      }

      try {
        // 역할을 영어로 변환
        String roleInEnglish = switch (role) {
          case "학생" -> "student";
          case "교수" -> "professor";
          case "조교" -> "ta";
          default -> role;
        };

        User newUser = new User(id, pw, name, dept, roleInEnglish);
        boolean success = reservationClient.registerUser(newUser);
        
        if (success) {
          JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다!");
          dispose();
          new LoginView(reservationClient).setVisible(true);
        } else {
          JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.");
        }
      } catch (Exception ex) {
        LOGGER.log(Level.SEVERE, "Error during signup: " + ex.getMessage(), ex);
        System.err.println("회원가입 중 오류 발생: " + ex.getMessage());
        ex.printStackTrace();
      }
    });

    btnCancel.addActionListener(e -> {
      dispose();
      new LoginView(reservationClient).setVisible(true);
    });
  }
}