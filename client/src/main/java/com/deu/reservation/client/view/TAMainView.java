package com.deu.reservation.client.view;

import com.deu.reservation.client.ReservationClient;
import com.deu.reservation.client.model.Reservation;
import com.deu.reservation.client.model.Room;
import com.deu.reservation.client.model.User;
import com.deu.reservation.client.model.ReservationStatus;
import com.deu.reservation.client.service.ReservationService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TAMainView extends MainView {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private DefaultListModel<Room> roomListModel;
    private JList<Room> roomList;
    private Room selectedRoom;
    private JComboBox<String> roomTypeComboBox;

    public TAMainView(User user, ReservationService reservationService, ReservationClient reservationClient) {
        super(user, reservationService, reservationClient);
    }

    @Override
    public String getTitle() {
        return "TA 조교 - 강의실 예약 시스템 - " + currentUser.getName();
    }

    @Override
    public void initializeContent() {
        // 메인 패널 설정
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 환영 메시지
        JLabel welcomeLabel = new JLabel("환영합니다, " + currentUser.getName() + " 조교님!");
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // 승인 대기 예약 목록
        JPanel pendingPanel = new JPanel(new BorderLayout());
        pendingPanel.setBorder(BorderFactory.createTitledBorder("승인 대기 예약 목록"));

        // 예약 목록 테이블
        String[] columnNames = {"예약자", "강의실", "날짜", "시작 시간", "종료 시간", "목적", "상태"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable reservationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        pendingPanel.add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton approveButton = new JButton("승인");
        JButton rejectButton = new JButton("거절");
        JButton cancelButton = new JButton("취소");

        approveButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "승인할 예약을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String reservationId = (String) reservationTable.getValueAt(selectedRow, 0);
            if (reservationService.approveReservation(reservationId, currentUser)) {
                JOptionPane.showMessageDialog(this, "예약이 승인되었습니다.");
                refreshReservationList(tableModel);
            } else {
                JOptionPane.showMessageDialog(this, "예약 승인에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        rejectButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "거절할 예약을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String reservationId = (String) reservationTable.getValueAt(selectedRow, 0);
            if (reservationService.rejectReservation(reservationId, currentUser)) {
                JOptionPane.showMessageDialog(this, "예약이 거절되었습니다.");
                refreshReservationList(tableModel);
            } else {
                JOptionPane.showMessageDialog(this, "예약 거절에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String reservationId = (String) reservationTable.getValueAt(selectedRow, 0);
            if (reservationService.cancelReservation(reservationId, currentUser)) {
                JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.");
                refreshReservationList(tableModel);
            } else {
                JOptionPane.showMessageDialog(this, "예약 취소에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(cancelButton);
        pendingPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(pendingPanel, BorderLayout.CENTER);

        // 초기 예약 목록 로드
        refreshReservationList(tableModel);

        // 강의실 목록 초기화
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRoom = roomList.getSelectedValue();
                updateRoomInfo();
            }
        });

        // 강의실 타입 선택
        String[] roomTypes = {"강의실", "실습실"};
        roomTypeComboBox = new JComboBox<>(roomTypes);
        roomTypeComboBox.addActionListener(e -> refreshRoomList());
    }

    @Override
    protected void showNewReservationDialog() {
        JDialog dialog = new JDialog(this, "새 예약", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 강의실 유형 선택
        panel.add(new JLabel("강의실 유형:"));
        String[] roomTypes = {"강의실", "실습실"};
        JComboBox<String> roomTypeComboBox = new JComboBox<>(roomTypes);
        panel.add(roomTypeComboBox);

        // 강의실 선택
        panel.add(new JLabel("강의실:"));
        DefaultListModel<Room> roomListModel = new DefaultListModel<>();
        JList<Room> roomList = new JList<>(roomListModel);
        JScrollPane roomScroll = new JScrollPane(roomList);
        panel.add(roomScroll);

        // 날짜 선택
        panel.add(new JLabel("날짜:"));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        panel.add(dateSpinner);

        // 시작 시간 선택
        panel.add(new JLabel("시작 시간:"));
        String[] startTimes = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
        JComboBox<String> startTimeComboBox = new JComboBox<>(startTimes);
        panel.add(startTimeComboBox);

        // 종료 시간 선택
        panel.add(new JLabel("종료 시간:"));
        String[] endTimes = {"10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};
        JComboBox<String> endTimeComboBox = new JComboBox<>(endTimes);
        panel.add(endTimeComboBox);

        // 목적 입력
        panel.add(new JLabel("목적:"));
        JTextArea purposeArea = new JTextArea(3, 20);
        JScrollPane purposeScroll = new JScrollPane(purposeArea);
        panel.add(purposeScroll);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton reserveButton = new JButton("예약하기");
        JButton cancelButton = new JButton("취소");

        reserveButton.addActionListener(e -> {
            try {
                LocalDate date = ((java.util.Date) dateSpinner.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                
                LocalTime startTime = LocalTime.parse((String) startTimeComboBox.getSelectedItem(), timeFormatter);
                LocalTime endTime = LocalTime.parse((String) endTimeComboBox.getSelectedItem(), timeFormatter);

                if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "종료 시간은 시작 시간보다 늦어야 합니다.", 
                        "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
                LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

                Room selectedRoom = roomList.getSelectedValue();
                String purpose = purposeArea.getText();

                if (selectedRoom == null) {
                    JOptionPane.showMessageDialog(dialog, "강의실을 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (purpose.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "목적을 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (reservationService.createReservation(
                        currentUser.getId(),
                        selectedRoom.getId(),
                        startDateTime,
                        endDateTime,
                        purpose,
                        currentUser)) {
                    JOptionPane.showMessageDialog(dialog, "예약이 완료되었습니다.");
                    dialog.dispose();
                    refreshContent();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "예약에 실패했습니다.\n" +
                        "- 평일(월~금) 09:00 ~ 18:00 사이의 시간만 예약 가능합니다.\n" +
                        "- 해당 시간에 이미 예약이 있습니다.", 
                        "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "잘못된 입력입니다.\n" +
                    "- 평일(월~금) 09:00 ~ 18:00 사이의 시간만 입력 가능합니다.", 
                    "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(reserveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    @Override
    protected void showMyReservations() {
        JDialog dialog = new JDialog(this, "내 예약", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        List<Reservation> reservations = reservationService.getUserReservations(currentUser.getId());
        JList<Reservation> reservationList = new JList<>(reservations.toArray(new Reservation[0]));
        reservationList.setCellRenderer(new ReservationListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(reservationList);
        dialog.add(scrollPane);

        dialog.setVisible(true);
    }

    @Override
    protected void showPendingReservations() {
        JDialog dialog = new JDialog(this, "승인 대기 예약", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        List<Reservation> pendingReservations = reservationService.getPendingReservations();
        JList<Reservation> reservationList = new JList<>(pendingReservations.toArray(new Reservation[0]));
        reservationList.setCellRenderer(new ReservationListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(reservationList);
        dialog.add(scrollPane);

        // 승인/거절 버튼
        JPanel buttonPanel = new JPanel();
        JButton approveButton = new JButton("승인");
        JButton rejectButton = new JButton("거절");

        approveButton.addActionListener(e -> {
            Reservation selected = reservationList.getSelectedValue();
            if (selected != null) {
                if (reservationService.approveReservation(selected.getId(), currentUser)) {
                    JOptionPane.showMessageDialog(dialog, "예약이 승인되었습니다.");
                    dialog.dispose();
                    refreshContent();
                } else {
                    JOptionPane.showMessageDialog(dialog, "승인에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "예약을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        rejectButton.addActionListener(e -> {
            Reservation selected = reservationList.getSelectedValue();
            if (selected != null) {
                if (reservationService.rejectReservation(selected.getId(), currentUser)) {
                    JOptionPane.showMessageDialog(dialog, "예약이 거절되었습니다.");
                    dialog.dispose();
                    refreshContent();
                } else {
                    JOptionPane.showMessageDialog(dialog, "거절에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "예약을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    @Override
    protected boolean canApproveReservations() {
        return true;
    }

    private void refreshContent() {
        mainPanel.removeAll();
        initializeContent();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void refreshRoomList() {
        String selectedType = (String) roomTypeComboBox.getSelectedItem();
        roomListModel.removeAllElements();
        List<Room> rooms = reservationService.getRoomsByType(selectedType);
        for (Room room : rooms) {
            roomListModel.addElement(room);
        }
    }

    private void updateRoomInfo() {
        // Implementation of updateRoomInfo method
    }

    private JPanel createFormPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(245, 245, 245));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private class ReservationListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                     boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Reservation) {
                Reservation reservation = (Reservation) value;
                setText(String.format("%s - %s (%s)",
                    reservation.getStartTime().format(formatter),
                    reservation.getEndTime().format(formatter),
                    reservation.getStatus()));
            }
            return this;
        }
    }

    private void refreshReservationList(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Reservation> pendingReservations = reservationService.getPendingReservations();
        
        for (Reservation reservation : pendingReservations) {
            Object[] row = {
                reservation.getUser().getName(),
                reservation.getRoom().getName(),
                reservation.getStartTime().format(dateFormatter),
                reservation.getStartTime().format(timeFormatter),
                reservation.getEndTime().format(timeFormatter),
                reservation.getPurpose(),
                reservation.getStatus()
            };
            tableModel.addRow(row);
        }
    }
} 