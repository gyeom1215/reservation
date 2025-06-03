package view.professor;

import model.professor.ProfessorApprovedModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProfessorApprovedView extends JFrame {

    public ProfessorApprovedView(List<ProfessorApprovedModel> schedules) {
        setTitle("나의 강의실 예약 정보");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창만 닫기
        setSize(700, 200);
        setLocationRelativeTo(null);

        String[] columns = {"직책", "방번호", "이름", "예약한 시간대", "예약 상태", "요일", "실습실/일반실"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        for (ProfessorApprovedModel s : schedules) {
            tableModel.addRow(new Object[]{
                    s.getRole(),
                    s.getRoomNumber(),
                    s.getName(),
                    String.join(", ", s.getTimeSlots()),
                    s.getState(),
                    s.getDay(),
                    s.getRoomType()
            });
        }

        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        // 아래에 "확인" 버튼 패널 추가
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnOk = new JButton("확인");
        btnOk.addActionListener(e -> dispose()); // 확인 누르면 창만 닫기
        buttonPanel.add(btnOk);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
}