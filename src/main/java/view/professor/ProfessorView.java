package view.professor;

import client.ReservationClient;
import model.common.Reservation;
import model.user.User;
import view.login.LoginView;
import model.room.RoomModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfessorView extends JFrame {
    private final User user;
    private final ReservationClient reservationClient;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> roomCombo;
    private JComboBox<String> dayCombo;
    private JList<String> timeSlotList;
    private String roomType;
    private JButton btnLogout;
    private JButton btnStartReservation;
    private java.util.function.Consumer<model.common.Reservation> reservationHandler;
    public RoomModel roomModel;
    private static final Logger LOGGER = Logger.getLogger(ProfessorView.class.getName());

    public ProfessorView(User user, ReservationClient reservationClient) {
        this.user = user;
        this.reservationClient = reservationClient;
        try {
            this.roomModel = new RoomModel("src/main/resources/room_data.json");
            initializeUI();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing ProfessorView: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(null,
                "강의실 데이터를 로드하는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginView(reservationClient).setVisible(true);
        }
    }

    private void initializeUI() {
        setTitle("교수 예약 시스템 - " + user.getName() + "님");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 상단 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel welcomeLabel = new JLabel(user.getName() + "님 환영합니다!");
        topPanel.add(welcomeLabel);

        // 로그아웃 버튼 추가
        btnLogout = new JButton("로그아웃");
        btnLogout.addActionListener(e -> {
            // 모든 창 닫기
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame && window != this) {
                    window.dispose();
                }
            }
            dispose(); // 현재 창 닫기
            new LoginView(reservationClient).setVisible(true);
        });
        topPanel.add(btnLogout);

        // 중앙 패널
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // 예약 패널
        JPanel reservationPanel = new JPanel(new BorderLayout());
        reservationPanel.setBorder(BorderFactory.createTitledBorder("예약 관리"));
        
        // Create table
        String[] columns = {"강의실 유형", "강의실 번호", "요일", "시간대", "상태"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(reservationTable);
        reservationPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnStartReservation = new JButton("예약하기");
        JButton btnCancel = new JButton("예약 취소");
        JButton btnRefresh = new JButton("새로고침");

        btnStartReservation.addActionListener(e -> showReservationUI());
        btnCancel.addActionListener(e -> cancelSelectedReservation());
        btnRefresh.addActionListener(e -> refreshReservations());

        buttonPanel.add(btnStartReservation);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnRefresh);
        reservationPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 안내 패널
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("안내"));
        JTextArea infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setText(
            "강의실 예약 안내\n\n" +
            "1. 예약 가능 시간: 평일 09:00 ~ 18:00\n" +
            "2. 예약 단위: 1시간\n" +
            "3. 최대 예약 가능 시간: 3시간\n" +
            "4. 예약 취소는 사용 24시간 전까지 가능\n\n" +
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
            List<Reservation> reservations = reservationClient.getUserReservations(user.getName(), user.getRole());
            
            for (Reservation r : reservations) {
                tableModel.addRow(new Object[]{
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

    private void showReservationUI() {
        JFrame reservationFrame = new JFrame("강의실 예약");
        reservationFrame.setSize(400, 400);
        reservationFrame.setLayout(new GridLayout(5, 1));

        // Room type selection
        String[] roomTypes = {"실습실", "일반실"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        roomTypeCombo.addActionListener(e -> {
            roomType = (String) roomTypeCombo.getSelectedItem();
            updateRoomCombo();
        });
        reservationFrame.add(labeledPanel("강의실 유형:", roomTypeCombo));

        // Room selection
        roomCombo = new JComboBox<>();
        roomCombo.addActionListener(e -> updateDayCombo());
        reservationFrame.add(labeledPanel("강의실 선택:", roomCombo));

        // Day selection
        dayCombo = new JComboBox<>();
        dayCombo.addActionListener(e -> updateTimeSlots());
        reservationFrame.add(labeledPanel("요일 선택:", dayCombo));

        // Time slot selection
        timeSlotList = new JList<>();
        timeSlotList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        reservationFrame.add(labeledPanel("시간대 선택:", new JScrollPane(timeSlotList)));

        // Reserve button
        JButton btnReserve = new JButton("예약하기");
        btnReserve.addActionListener(e -> handleReservation());
        reservationFrame.add(btnReserve);

        roomType = (String) roomTypeCombo.getSelectedItem();
        updateRoomCombo();
        reservationFrame.setVisible(true);
    }

    private JPanel labeledPanel(String label, Component component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void updateRoomCombo() {
        if (reservationClient == null) return;

        roomCombo.removeAllItems();
        Set<Integer> roomNumbers = roomModel.getRoomsByType(roomType);
        
        for (int roomNumber : roomNumbers) {
            roomCombo.addItem(roomNumber);
        }
        updateDayCombo();
    }

    private void updateDayCombo() {
        if (reservationClient == null) return;

        Integer room = (Integer) roomCombo.getSelectedItem();
        if (room == null) return;

        dayCombo.removeAllItems();
        Set<String> days = getDays(room);
        for (String day : days) {
            dayCombo.addItem(day);
        }
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        if (reservationClient == null) return;

        Integer room = (Integer) roomCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        if (room == null || day == null) return;

        List<String> slots = getTimeSlots(room, day);
        timeSlotList.setListData(slots.toArray(new String[0]));
    }

    private void handleReservation() {
        if (reservationClient == null) return;

        Integer room = (Integer) roomCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        List<String> selectedSlots = timeSlotList.getSelectedValuesList();

        if (selectedSlots.size() > 3) {
            JOptionPane.showMessageDialog(this, "최대 3개의 시간대만 선택할 수 있습니다.");
            return;
        }

        String state = user.getRole().equalsIgnoreCase("professor") ? "승인" : "대기";
        Reservation reservation = new Reservation(
            user.getName(),
            user.getRole(),
            roomType,
            room,
            day,
            selectedSlots,
            state
        );

        if (reservationClient.makeReservation(reservation)) {
            JOptionPane.showMessageDialog(this, "예약이 완료되었습니다.");
            refreshReservations();
        } else {
            JOptionPane.showMessageDialog(this, "예약에 실패했습니다.");
        }
    }

    private void cancelSelectedReservation() {
        if (reservationClient == null) return;

        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.");
            return;
        }

        Reservation reservation = getReservationFromRow(selectedRow);
        if (reservationClient.cancelReservation(reservation)) {
            refreshReservations();
            JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "예약 취소에 실패했습니다.");
        }
    }

    private Reservation getReservationFromRow(int row) {
        return new Reservation(
            user.getName(),
            user.getRole(),
            (String) tableModel.getValueAt(row, 0), // roomType
            (Integer) tableModel.getValueAt(row, 1), // roomNumber
            (String) tableModel.getValueAt(row, 2), // day
            List.of(((String) tableModel.getValueAt(row, 3)).split(", ")), // timeSlots
            (String) tableModel.getValueAt(row, 4)  // state
        );
    }

    private Set<Integer> getRoomNumbers() {
        return roomModel.getRoomNumbers();
    }

    private Set<String> getDays(int roomNumber) {
        return roomModel.getDays(roomNumber);
    }

    private List<String> getTimeSlots(int roomNumber, String day) {
        return roomModel.getTimeSlots(roomNumber, day);
    }

    public void setReservationHandler(java.util.function.Consumer<model.common.Reservation> handler) {
        this.reservationHandler = handler;
    }

    public JButton getBtnStartReservation() {
        return btnStartReservation;
    }

    public JButton getBtnLogout() {
        return btnLogout;
    }

    public ReservationClient getReservationClient() {
        return reservationClient;
    }

    public RoomModel getRoomModel() {
        return roomModel;
    }

    public void showReservationUI(String jsonPath, String roomType) {
        this.roomType = roomType;
        showReservationUI();
    }
}