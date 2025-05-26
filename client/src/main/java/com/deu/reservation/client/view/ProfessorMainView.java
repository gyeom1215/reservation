package com.deu.reservation.client.view;

import com.deu.reservation.client.ReservationClient;
import com.deu.reservation.client.model.Reservation;
import com.deu.reservation.client.model.Room;
import com.deu.reservation.client.model.User;
import com.deu.reservation.client.model.ReservationStatus;
import com.deu.reservation.client.service.ReservationService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

public class ProfessorMainView extends MainView {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private JTable reservationTable;
    private JTable pendingTable;
    private JList<Room> roomList;
    private DefaultListModel<Room> roomListModel;
    private JComboBox<String> roomTypeComboBox;
    private Room selectedRoom;

    public ProfessorMainView(User user, ReservationService reservationService, ReservationClient reservationClient) {
        super(user, reservationService, reservationClient);
    }

    @Override
    public String getTitle() {
        return "교수 - 강의실 예약 시스템 - " + currentUser.getName();
    }

    @Override
    public void initializeContent() {
        // 예약 목록 테이블
        String[] columnNames = {"강의실", "날짜", "시작 시간", "종료 시간", "목적", "상태"};
        Object[][] data = new Object[0][columnNames.length];
        reservationTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        refreshReservationList();

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

    private void refreshReservationList() {
        List<Reservation> reservations = reservationService.getUserReservations(currentUser.getId());
        Object[][] data = new Object[reservations.size()][6];
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            data[i][0] = r.getRoomName();
            data[i][1] = r.getStartTime().toLocalDate();
            data[i][2] = r.getStartTime().toLocalTime();
            data[i][3] = r.getEndTime().toLocalTime();
            data[i][4] = r.getPurpose();
            data[i][5] = r.getStatus();
        }
        reservationTable.setModel(new javax.swing.table.DefaultTableModel(data, 
            new String[]{"강의실", "날짜", "시작 시간", "종료 시간", "목적", "상태"}));
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
                    refreshReservationList();
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
        refreshReservationList();
    }

    @Override
    protected void showPendingReservations() {
        // 교수는 승인 대기 예약을 볼 필요가 없음
    }

    @Override
    protected boolean canApproveReservations() {
        return false;
    }

    private void refreshRoomList() {
        roomListModel.clear();
        List<Room> rooms = reservationService.getRoomsByType((String) roomTypeComboBox.getSelectedItem());
        for (Room room : rooms) {
            roomListModel.addElement(room);
        }
    }

    private void updateRoomInfo() {
        // Implementation of updateRoomInfo method
    }
} 