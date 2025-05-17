package com.example.classroomreservation.ui;

import com.example.classroomreservation.model.Reservation;
import com.example.classroomreservation.service.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;

public class StudentReservationFrame extends JFrame {
    private final ReservationService reservationService;
    private final String currentStudentId; // 로그인된 학생 ID (여기서는 하드코딩 또는 입력받음)

    private JComboBox<String> classroomComboBox;
    private JTextField dateField; // YYYY-MM-DD
    private JComboBox<String> startTimeComboBox;
    private JComboBox<String> endTimeComboBox;
    private JTextField purposeField;
    private JTextField studentIdField; // 학생 ID 입력 필드

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    private JButton viewAllButton;
    private JButton viewMyButton;


    public StudentReservationFrame(String studentId) {
        this.currentStudentId = studentId; // 생성자에서 학생 ID를 받도록 수정
        this.reservationService = new ReservationService();

        setTitle("강의실 예약 시스템 (학생: " + currentStudentId + ")");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadAllReservationsToTable(); // 초기에는 모든 예약 표시
    }
    
    public StudentReservationFrame() { // 기본 생성자 (학생 ID 입력용)
        this.currentStudentId = null; // 아직 학생 ID 모름
        this.reservationService = new ReservationService();

        setTitle("강의실 예약 시스템");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadAllReservationsToTable();
    }


    private void initComponents() {
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 가변 행, 2열
        inputPanel.setBorder(BorderFactory.createTitledBorder("새 예약 / 정보 입력"));

        inputPanel.add(new JLabel("강의실:"));
        classroomComboBox = new JComboBox<>(reservationService.getAvailableClassrooms());
        inputPanel.add(classroomComboBox);

        inputPanel.add(new JLabel("날짜 (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("시작 시간:"));
        startTimeComboBox = new JComboBox<>(ReservationService.AVAILABLE_TIMES);
        inputPanel.add(startTimeComboBox);
        
        inputPanel.add(new JLabel("종료 시간:"));
        endTimeComboBox = new JComboBox<>(ReservationService.AVAILABLE_TIMES);
        // 종료 시간은 시작 시간보다 1시간 뒤로 기본 설정
        startTimeComboBox.addActionListener(e -> {
            int startIndex = startTimeComboBox.getSelectedIndex();
            if (startIndex < ReservationService.AVAILABLE_TIMES.length -1) {
                endTimeComboBox.setSelectedIndex(startIndex + 1);
            } else {
                 endTimeComboBox.setSelectedIndex(startIndex); // 마지막 시간이면 동일하게
            }
        });
        // 초기 endTimeComboBox 설정
        if (ReservationService.AVAILABLE_TIMES.length > 1) {
            endTimeComboBox.setSelectedIndex(1); 
        } else if (ReservationService.AVAILABLE_TIMES.length > 0) {
             endTimeComboBox.setSelectedIndex(0);
        }

        inputPanel.add(endTimeComboBox);


        inputPanel.add(new JLabel("예약 목적:"));
        purposeField = new JTextField();
        inputPanel.add(purposeField);

        inputPanel.add(new JLabel("학생 ID:"));
        studentIdField = new JTextField(currentStudentId != null ? currentStudentId : ""); // 로그인된 ID 기본값
        if (currentStudentId != null) {
            studentIdField.setEditable(false); // 로그인 ID 있으면 수정 불가
        }
        inputPanel.add(studentIdField);


        JButton makeReservationButton = new JButton("예약하기");
        makeReservationButton.addActionListener(this::performMakeReservation);
        inputPanel.add(makeReservationButton);

        JButton cancelButton = new JButton("선택한 내 예약 취소");
        cancelButton.addActionListener(this::performCancelReservation);
        inputPanel.add(cancelButton);

        add(inputPanel, BorderLayout.NORTH);

        // Reservation List Panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("예약 현황"));
        
        tableModel = new DefaultTableModel(new String[]{"ID", "강의실", "날짜", "시작", "종료", "학생ID", "목적"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 테이블 내용 수정 불가
            }
        };
        reservationTable = new JTable(tableModel);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // ID 컬럼 숨기기 (사용자에게 불필요)
        // reservationTable.getColumnModel().getColumn(0).setMinWidth(0);
        // reservationTable.getColumnModel().getColumn(0).setMaxWidth(0);
        // reservationTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // View control buttons
        JPanel viewControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewAllButton = new JButton("모든 예약 보기");
        viewAllButton.addActionListener(e -> loadAllReservationsToTable());
        viewMyButton = new JButton("내 예약 보기");
        viewMyButton.addActionListener(e -> {
            String sid = studentIdField.getText().trim();
            if (sid.isEmpty() && currentStudentId == null) {
                JOptionPane.showMessageDialog(this, "학생 ID를 입력하고 '내 예약 보기'를 시도해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            loadMyReservationsToTable(currentStudentId != null ? currentStudentId : sid);
        });
        
        // 학생 ID가 초기에 주입되지 않았다면 '내 예약 보기'는 비활성화, ID 입력 후 활성화
        if (currentStudentId == null) {
            viewMyButton.setEnabled(false);
            studentIdField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateViewMyButtonState(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { updateViewMyButtonState(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { updateViewMyButtonState(); }
            });
        }


        viewControlPanel.add(viewAllButton);
        viewControlPanel.add(viewMyButton);
        listPanel.add(viewControlPanel, BorderLayout.SOUTH);

        add(listPanel, BorderLayout.CENTER);
    }
    
    private void updateViewMyButtonState() {
        if (viewMyButton != null) { // 컴포넌트가 초기화된 후에만 실행
             viewMyButton.setEnabled(!studentIdField.getText().trim().isEmpty());
        }
    }

    private void performMakeReservation(ActionEvent e) {
        String classroomId = (String) classroomComboBox.getSelectedItem();
        String date = dateField.getText().trim();
        String startTime = (String) startTimeComboBox.getSelectedItem();
        String endTime = (String) endTimeComboBox.getSelectedItem();
        String purpose = purposeField.getText().trim();
        String studentIdToUse = studentIdField.getText().trim();

        if (studentIdToUse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "학생 ID를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (date.isEmpty() || !isValidDate(date)) {
             JOptionPane.showMessageDialog(this, "올바른 날짜 형식(YYYY-MM-DD)으로 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (startTime == null || endTime == null || startTime.equals(endTime) || 
            ReservationService.AVAILABLE_TIMES[startTimeComboBox.getSelectedIndex()].compareTo(ReservationService.AVAILABLE_TIMES[endTimeComboBox.getSelectedIndex()]) >= 0) {
            JOptionPane.showMessageDialog(this, "시작 시간은 종료 시간보다 빨라야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }


        if (reservationService.makeReservation(classroomId, studentIdToUse, date, startTime, endTime, purpose)) {
            JOptionPane.showMessageDialog(this, "예약되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            // 현재 '모든 예약 보기' 상태면 전체 로드, '내 예약 보기' 상태면 내 예약 로드
            if (viewMyButton.isEnabled() && !viewAllButton.isEnabled()) { // 좀 더 정확한 상태 체크 필요
                 loadMyReservationsToTable(studentIdToUse);
            } else {
                 loadAllReservationsToTable();
            }
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this, "예약에 실패했습니다. 해당 시간에 이미 다른 예약이 있거나 시간이 잘못되었습니다.", "실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void performCancelReservation(ActionEvent e) {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "취소할 예약을 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reservationId = (String) tableModel.getValueAt(selectedRow, 0); // ID는 첫 번째 컬럼
        String bookedStudentId = (String) tableModel.getValueAt(selectedRow, 5); // 학생 ID는 6번째 컬럼
        String studentIdToUse = studentIdField.getText().trim();
        
        if (studentIdToUse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "본인 확인을 위해 학생 ID를 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!bookedStudentId.equals(studentIdToUse)) {
             JOptionPane.showMessageDialog(this, "본인의 예약만 취소할 수 있습니다.", "권한 없음", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "정말로 이 예약을 취소하시겠습니까?", "예약 취소 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (reservationService.cancelReservation(reservationId, studentIdToUse)) {
                JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                 if (viewMyButton.isEnabled() && !viewAllButton.isEnabled()) {
                    loadMyReservationsToTable(studentIdToUse);
                } else {
                    loadAllReservationsToTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "예약 취소에 실패했습니다. (존재하지 않거나 본인 예약 아님)", "실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadReservationsToTable(List<Reservation> reservations) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Reservation r : reservations) {
            Vector<String> row = new Vector<>();
            row.add(r.getId());
            row.add(r.getClassroomId());
            row.add(r.getDate());
            row.add(r.getStartTime());
            row.add(r.getEndTime());
            row.add(r.getStudentId());
            row.add(r.getPurpose());
            tableModel.addRow(row);
        }
    }
    
    private void loadAllReservationsToTable() {
        List<Reservation> allReservations = reservationService.getAllReservations();
        loadReservationsToTable(allReservations);
        viewAllButton.setEnabled(false); // 현재 모든 예약 보기 상태
        if (currentStudentId != null || !studentIdField.getText().trim().isEmpty()) {
             viewMyButton.setEnabled(true);
        }
    }

    private void loadMyReservationsToTable(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()){
             JOptionPane.showMessageDialog(this, "학생 ID를 입력해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        List<Reservation> myReservations = reservationService.getMyReservations(studentId);
        loadReservationsToTable(myReservations);
        viewMyButton.setEnabled(false); // 현재 내 예약 보기 상태
        viewAllButton.setEnabled(true);
    }

    private void clearInputFields() {
        // dateField는 오늘 날짜로 유지하거나 비울 수 있음
        // studentIdField는 로그인 ID가 있다면 그대로 둠
        purposeField.setText("");
        classroomComboBox.setSelectedIndex(0);
        startTimeComboBox.setSelectedIndex(0);
        // endTimeComboBox는 startTimeComboBox 리스너에 의해 자동 업데이트됨
    }
}