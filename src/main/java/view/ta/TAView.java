package view.ta;

import client.ReservationClient;
import model.common.Reservation;
import model.user.User;
import view.login.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TAView extends JFrame {
    private final User user;
    private final ReservationClient reservationClient;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private static final Logger LOGGER = Logger.getLogger(TAView.class.getName());

    public TAView(User user, ReservationClient reservationClient) {
        this.user = user;
        this.reservationClient = reservationClient;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("조교 예약 시스템 - " + user.getName() + "님");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 상단 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel welcomeLabel = new JLabel(user.getName() + "님 환영합니다!");
        topPanel.add(welcomeLabel);

        // 로그아웃 버튼 추가
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginView(reservationClient).setVisible(true);
        });
        topPanel.add(logoutButton);

        // 중앙 패널
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // 예약 패널
        JPanel reservationPanel = new JPanel(new BorderLayout());
        reservationPanel.setBorder(BorderFactory.createTitledBorder("예약 관리"));
        
        // Create table
        String[] columns = {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(reservationTable);
        reservationPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        JButton approveButton = new JButton("승인");
        JButton rejectButton = new JButton("거절");
        JButton refreshButton = new JButton("새로고침");
        JButton viewApprovedButton = new JButton("승인 내역");
        JButton viewRejectedButton = new JButton("거절 내역");
        JButton manageUsersButton = new JButton("사용자 관리");

        approveButton.addActionListener(e -> approveSelectedReservation());
        rejectButton.addActionListener(e -> rejectSelectedReservation());
        refreshButton.addActionListener(e -> refreshReservations());
        viewApprovedButton.addActionListener(e -> showApprovedReservations());
        viewRejectedButton.addActionListener(e -> showRejectedReservations());
        manageUsersButton.addActionListener(e -> {
            LOGGER.info("사용자 관리 버튼 클릭됨");
            try {
                showUserManagement();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "사용자 관리 화면 열기 실패: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(this, "사용자 관리 화면을 열 수 없습니다: " + ex.getMessage());
            }
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewApprovedButton);
        buttonPanel.add(viewRejectedButton);
        buttonPanel.add(manageUsersButton);
        reservationPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 안내 패널
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("안내"));
        JTextArea infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setText(
            "예약 승인 안내\n\n" +
            "1. 학생/교수의 예약 요청을 검토합니다.\n" +
            "2. 시간이 겹치지 않는지 확인합니다.\n" +
            "3. 승인 또는 거절 처리를 합니다.\n" +
            "4. 거절 시 사유를 입력해주세요.\n\n" +
            "문의사항: 학과 사무실"
        );
        infoPanel.add(new JScrollPane(infoTextArea), BorderLayout.CENTER);

        centerPanel.add(reservationPanel);
        centerPanel.add(infoPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Initial load
        refreshReservations();
        setVisible(true);
    }

    private void refreshReservations() {
        if (reservationClient == null) return;

        tableModel.setRowCount(0);
        try {
            List<Reservation> reservations = reservationClient.getAllReservations();
            
            for (Reservation r : reservations) {
                tableModel.addRow(new Object[]{
                    r.getName(),
                    r.getRole(),
                    r.getRoomType(),
                    r.getRoomNumber(),
                    r.getDay(),
                    String.join(", ", r.getTimeSlots()),
                    r.getState()
                });
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading reservation data: " + ex.getMessage(), ex);
            System.err.println("예약 데이터 로드 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void approveSelectedReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "승인할 예약을 선택해주세요.");
            return;
        }

        Reservation reservation = getReservationFromRow(selectedRow);
        if (reservationClient.updateReservationState(reservation, "승인")) {
            refreshReservations();
            JOptionPane.showMessageDialog(this, "예약이 승인되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "예약 승인에 실패했습니다.");
        }
    }

    private void rejectSelectedReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "거절할 예약을 선택해주세요.");
            return;
        }

        Reservation reservation = getReservationFromRow(selectedRow);
        if (reservationClient.updateReservationState(reservation, "거절")) {
            refreshReservations();
            JOptionPane.showMessageDialog(this, "예약이 거절되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "예약 거절에 실패했습니다.");
        }
    }

    private Reservation getReservationFromRow(int row) {
        return new Reservation(
            (String) tableModel.getValueAt(row, 0), // name
            (String) tableModel.getValueAt(row, 1), // role
            (String) tableModel.getValueAt(row, 2), // roomType
            (Integer) tableModel.getValueAt(row, 3), // roomNumber
            (String) tableModel.getValueAt(row, 4), // day
            List.of(((String) tableModel.getValueAt(row, 5)).split(", ")), // timeSlots
            (String) tableModel.getValueAt(row, 6)  // state
        );
    }

    private void showApprovedReservations() {
        JFrame approvedFrame = new JFrame("승인된 예약 내역");
        approvedFrame.setSize(800, 600);
        approvedFrame.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create table
        String[] columns = {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"};
        DefaultTableModel approvedModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable approvedTable = new JTable(approvedModel);
        JScrollPane tableScrollPane = new JScrollPane(approvedTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Load approved reservations
        List<Reservation> reservations = reservationClient.getAllReservations();
        for (Reservation r : reservations) {
            if ("승인".equals(r.getState())) {
                approvedModel.addRow(new Object[]{
                    r.getName(),
                    r.getRole(),
                    r.getRoomType(),
                    r.getRoomNumber(),
                    r.getDay(),
                    String.join(", ", r.getTimeSlots()),
                    r.getState()
                });
            }
        }

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> approvedFrame.dispose());
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        approvedFrame.add(mainPanel);
        approvedFrame.setVisible(true);
    }

    private void showRejectedReservations() {
        JFrame rejectedFrame = new JFrame("거절된 예약 내역");
        rejectedFrame.setSize(800, 600);
        rejectedFrame.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create table
        String[] columns = {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"};
        DefaultTableModel rejectedModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable rejectedTable = new JTable(rejectedModel);
        JScrollPane tableScrollPane = new JScrollPane(rejectedTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Load rejected reservations
        List<Reservation> reservations = reservationClient.getAllReservations();
        for (Reservation r : reservations) {
            if ("거절".equals(r.getState())) {
                rejectedModel.addRow(new Object[]{
                    r.getName(),
                    r.getRole(),
                    r.getRoomType(),
                    r.getRoomNumber(),
                    r.getDay(),
                    String.join(", ", r.getTimeSlots()),
                    r.getState()
                });
            }
        }

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> rejectedFrame.dispose());
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        rejectedFrame.add(mainPanel);
        rejectedFrame.setVisible(true);
    }

    private void showUserManagement() {
        LOGGER.info("showUserManagement 메서드 시작");
        try {
            // EDT에서 실행되는지 확인
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(this::showUserManagement);
                return;
            }

            JFrame userFrame = new JFrame("사용자 계정 관리");
            userFrame.setSize(800, 600);
            userFrame.setLocationRelativeTo(this);
            userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Create table - 컬럼 순서: 아이디, 이름, 학과, 역할
            String[] columns = {"아이디", "이름", "학과", "역할"};
            DefaultTableModel userModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable userTable = new JTable(userModel);
            JScrollPane tableScrollPane = new JScrollPane(userTable);
            mainPanel.add(tableScrollPane, BorderLayout.CENTER);

            // Load users
            LOGGER.info("사용자 목록 로드 시작");
            List<User> users = reservationClient.getAllUsers();
            LOGGER.info("로드된 사용자 수: " + users.size());
            
            for (User u : users) {
                String role = u.getRole();
                String department = u.getDepartment();
                
                // 역할 한글화
                if ("ta".equals(role)) {
                    role = "조교";
                } else if ("professor".equals(role)) {
                    role = "교수";
                } else if ("student".equals(role)) {
                    role = "학생";
                }
                
                LOGGER.info("사용자 추가: " + u.getId() + ", " + u.getName() + ", " + role + ", " + department);
                userModel.addRow(new Object[]{
                    u.getId(),
                    u.getName(),
                    department,
                    role
                });
            }

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton addButton = new JButton("사용자 추가");
            JButton editButton = new JButton("사용자 수정");
            JButton deleteButton = new JButton("사용자 삭제");
            JButton closeButton = new JButton("닫기");

            addButton.addActionListener(e -> {
                LOGGER.info("사용자 추가 버튼 클릭");
                SwingUtilities.invokeLater(() -> addUser(userModel));
            });
            editButton.addActionListener(e -> {
                LOGGER.info("사용자 수정 버튼 클릭");
                SwingUtilities.invokeLater(() -> editUser(userTable, userModel));
            });
            deleteButton.addActionListener(e -> {
                LOGGER.info("사용자 삭제 버튼 클릭");
                SwingUtilities.invokeLater(() -> deleteUser(userTable, userModel));
            });
            closeButton.addActionListener(e -> {
                LOGGER.info("사용자 관리 창 닫기");
                userFrame.dispose();
            });

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            userFrame.add(mainPanel);
            userFrame.setVisible(true);
            LOGGER.info("사용자 관리 화면 표시 완료");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing user management: " + e.getMessage(), e);
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(this, 
                    "사용자 관리 화면을 열 수 없습니다: " + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE));
        }
    }

    private void addUser(DefaultTableModel model) {
        try {
            LOGGER.info("사용자 추가 시작");
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField passwordField = new JTextField();
            JComboBox<String> roleCombo = new JComboBox<>(new String[]{"student", "professor", "ta"});
            JTextField deptField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("아이디:"));
            panel.add(idField);
            panel.add(new JLabel("이름:"));
            panel.add(nameField);
            panel.add(new JLabel("비밀번호:"));
            panel.add(passwordField);
            panel.add(new JLabel("역할:"));
            panel.add(roleCombo);
            panel.add(new JLabel("학과:"));
            panel.add(deptField);

            int result = JOptionPane.showConfirmDialog(this, panel, "사용자 추가", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                LOGGER.info("사용자 추가 확인");
                User newUser = new User(
                    idField.getText(),
                    nameField.getText(),
                    passwordField.getText(),
                    (String) roleCombo.getSelectedItem(),
                    deptField.getText()
                );
                
                LOGGER.info("새 사용자 정보: " + newUser.getId() + ", " + newUser.getName());
                if (reservationClient.registerUser(newUser)) {
                    LOGGER.info("사용자 등록 성공");
                    model.addRow(new Object[]{
                        newUser.getId(),
                        newUser.getName(),
                        newUser.getDepartment(),
                        newUser.getRole()
                    });
                    JOptionPane.showMessageDialog(this, "사용자가 추가되었습니다.");
                } else {
                    LOGGER.warning("사용자 등록 실패");
                    JOptionPane.showMessageDialog(this, "사용자 추가에 실패했습니다.");
                }
            } else {
                LOGGER.info("사용자 추가 취소");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding user: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "사용자 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void editUser(JTable table, DefaultTableModel model) {
        try {
            LOGGER.info("사용자 수정 시작");
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                LOGGER.warning("수정할 사용자가 선택되지 않음");
                JOptionPane.showMessageDialog(this, "수정할 사용자를 선택해주세요.");
                return;
            }

            String id = (String) model.getValueAt(selectedRow, 0);
            String name = (String) model.getValueAt(selectedRow, 1);
            String role = (String) model.getValueAt(selectedRow, 2);
            String department = (String) model.getValueAt(selectedRow, 3);

            // 역할 영문으로 변환
            String roleEng = role;
            if ("조교".equals(role)) {
                roleEng = "ta";
            } else if ("교수".equals(role)) {
                roleEng = "professor";
            } else if ("학생".equals(role)) {
                roleEng = "student";
            }

            LOGGER.info("선택된 사용자: " + id + ", " + name + ", " + roleEng + ", " + department);

            JTextField nameField = new JTextField(name);
            JTextField passwordField = new JTextField();
            JComboBox<String> roleCombo = new JComboBox<>(new String[]{"student", "professor", "ta"});
            roleCombo.setSelectedItem(roleEng);
            JTextField deptField = new JTextField(department);

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("이름:"));
            panel.add(nameField);
            panel.add(new JLabel("새 비밀번호:"));
            panel.add(passwordField);
            panel.add(new JLabel("역할:"));
            panel.add(roleCombo);
            panel.add(new JLabel("학과:"));
            panel.add(deptField);

            int result = JOptionPane.showConfirmDialog(this, panel, "사용자 수정", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                LOGGER.info("사용자 수정 확인");
                User updatedUser = new User(
                    id,
                    nameField.getText(),
                    passwordField.getText().isEmpty() ? null : passwordField.getText(),
                    (String) roleCombo.getSelectedItem(),
                    deptField.getText()
                );
                
                LOGGER.info("수정된 사용자 정보: " + updatedUser.getId() + ", " + updatedUser.getName());
                if (reservationClient.updateUser(updatedUser)) {
                    LOGGER.info("사용자 정보 수정 성공");
                    model.setValueAt(updatedUser.getName(), selectedRow, 1);
                    
                    // 역할 한글화
                    String roleDisplay = updatedUser.getRole();
                    if ("ta".equals(roleDisplay)) {
                        roleDisplay = "조교";
                    } else if ("professor".equals(roleDisplay)) {
                        roleDisplay = "교수";
                    } else if ("student".equals(roleDisplay)) {
                        roleDisplay = "학생";
                    }
                    model.setValueAt(roleDisplay, selectedRow, 2);
                    model.setValueAt(updatedUser.getDepartment(), selectedRow, 3);
                    
                    JOptionPane.showMessageDialog(this, "사용자 정보가 수정되었습니다.");
                } else {
                    LOGGER.warning("사용자 정보 수정 실패");
                    JOptionPane.showMessageDialog(this, "사용자 정보 수정에 실패했습니다.");
                }
            } else {
                LOGGER.info("사용자 수정 취소");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error editing user: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "사용자 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void deleteUser(JTable table, DefaultTableModel model) {
        try {
            LOGGER.info("사용자 삭제 시작");
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                LOGGER.warning("삭제할 사용자가 선택되지 않음");
                JOptionPane.showMessageDialog(this, "삭제할 사용자를 선택해주세요.");
                return;
            }

            String id = (String) model.getValueAt(selectedRow, 0);
            LOGGER.info("선택된 사용자 ID: " + id);

            int confirm = JOptionPane.showConfirmDialog(this,
                "정말로 이 사용자를 삭제하시겠습니까?",
                "사용자 삭제",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                LOGGER.info("사용자 삭제 확인");
                if (reservationClient.deleteUser(id)) {
                    LOGGER.info("사용자 삭제 성공");
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "사용자가 삭제되었습니다.");
                } else {
                    LOGGER.warning("사용자 삭제 실패");
                    JOptionPane.showMessageDialog(this, "사용자 삭제에 실패했습니다.");
                }
            } else {
                LOGGER.info("사용자 삭제 취소");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "사용자 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 