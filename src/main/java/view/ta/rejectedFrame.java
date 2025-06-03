/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.ta;

import client.ReservationClient;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import model.common.Reservation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class rejectedFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(rejectedFrame.class.getName());
    private final ReservationClient reservationClient;
    private JTable table;
    private DefaultTableModel tableModel;

    public rejectedFrame(ReservationClient reservationClient) {
        if (reservationClient == null) {
            throw new IllegalArgumentException("ReservationClient cannot be null");
        }
        this.reservationClient = reservationClient;
        initializeFrame();
        initializeUI();
        loadRejectedReservations();
    }

    private void initializeFrame() {
        setTitle("거절된 예약");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeUI() {
        try {
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // 테이블 생성
            String[] columnNames = {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(tableModel);
            configureTableColumns();
            
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // 버튼 패널
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton backButton = new JButton("뒤로가기");
            backButton.addActionListener(e -> handleBackButton());
            buttonPanel.add(backButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing UI: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, 
                "UI 초기화 중 오류가 발생했습니다: " + e.getMessage(), 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configureTableColumns() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // 이름
        columnModel.getColumn(1).setPreferredWidth(80);  // 역할
        columnModel.getColumn(2).setPreferredWidth(100); // 강의실 유형
        columnModel.getColumn(3).setPreferredWidth(100); // 강의실 번호
        columnModel.getColumn(4).setPreferredWidth(80);  // 요일
        columnModel.getColumn(5).setPreferredWidth(200); // 시간대
        columnModel.getColumn(6).setPreferredWidth(80);  // 상태
    }

    private void loadRejectedReservations() {
        try {
            List<Reservation> allReservations = reservationClient.getAllReservations();
            tableModel.setRowCount(0);

            for (Reservation reservation : allReservations) {
                if ("거절".equals(reservation.getState())) {
                    tableModel.addRow(new Object[]{
                        reservation.getName(),
                        reservation.getRole(),
                        reservation.getRoomType(),
                        reservation.getRoomNumber(),
                        reservation.getDay(),
                        String.join(", ", reservation.getTimeSlots()),
                        reservation.getState()
                    });
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "거절된 예약이 없습니다.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading rejected reservations: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                "예약 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBackButton() {
        try {
            new featureFrame(reservationClient).setVisible(true);
            dispose();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling back button: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                "이전 화면으로 돌아가는 중 오류가 발생했습니다: " + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setRejectedTableModel(DefaultTableModel model) {
        if (model != null) {
            table.setModel(model);
        }
    }

    @Override
    public void dispose() {
        try {
            super.dispose();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error disposing frame: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error setting look and feel: " + e.getMessage(), e);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                server.ReservationServer reservationServer = new server.ReservationServer();
                client.ReservationClient reservationClient = client.ReservationClient.getInstance(reservationServer);
                new rejectedFrame(reservationClient).setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error starting application: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(null,
                    "애플리케이션을 시작하는 중 오류가 발생했습니다: " + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
