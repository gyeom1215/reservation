package view.ta;

import client.ReservationClient;
import javax.swing.*;
import java.awt.*;

public class ReservationFrame extends JFrame {
    private final ReservationClient reservationClient;

    public ReservationFrame(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;
        setTitle("예약 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 예약 테이블
        String[] columnNames = {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"};
        Object[][] data = {}; // 실제 데이터는 서버에서 가져와야 함
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton approveButton = new JButton("승인");
        JButton rejectButton = new JButton("거절");
        JButton backButton = new JButton("뒤로가기");

        approveButton.addActionListener(e -> {
            // 승인 로직 구현
        });

        rejectButton.addActionListener(e -> {
            // 거절 로직 구현
        });

        backButton.addActionListener(e -> {
            new featureFrame(reservationClient).setVisible(true);
            dispose();
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
} 