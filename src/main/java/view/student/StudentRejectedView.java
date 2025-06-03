package view.student;

import controller.student.StudentRejectedController;
import model.student.StudentApprovedModel;
import model.room.RoomModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentRejectedView extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private List<StudentApprovedModel> myReservationList;
    private static final Logger LOGGER = Logger.getLogger(StudentRejectedView.class.getName());

    public StudentRejectedView(
            String reservationFile,
            RoomModel roomModel,
            String LabRoomJsonPath,
            String NormalRoomJsonPath,
            String loginUserName,
            String loginUserRole
    ) {
        setTitle("예약 취소 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 260);
        setLocationRelativeTo(null);

        // 테이블 헤더
        String[] columns = {"직책", "방번호", "이름", "예약한 시간대", "예약 상태", "요일", "실습실/일반실"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);

        myReservationList = new java.util.ArrayList<>();

        // 데이터 로딩 및 필터링
        boolean found = false;
        try {
            myReservationList = controller.student.StudentRejectedController.getMyPendingOrApprovedReservations(
                    reservationFile, loginUserName, loginUserRole);

            if (myReservationList.isEmpty()) {
                tableModel.addRow(new Object[]{"등록된 예약이 없습니다.", "", "", "", "", "", ""});
                table.setEnabled(false);
            } else {
                for (StudentApprovedModel m : myReservationList) {
                    tableModel.addRow(new Object[]{
                            m.getRole(),
                            m.getRoomNumber(),
                            m.getName(),
                            safeJoin(m.getTimeSlots(), ", "),
                            m.getState(),
                            m.getDay(),
                            m.getRoomType()
                    });
                }
                table.setEnabled(true);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing reservation: " + ex.getMessage(), ex);
            System.err.println("예약 처리 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }

        // 레이아웃
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        // 하단 버튼
        JButton cancelBtn = new JButton("선택 예약 취소");
        JButton closeBtn = new JButton("닫기");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelBtn);
        buttonPanel.add(closeBtn);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // 취소 버튼 이벤트
        cancelBtn.addActionListener(e -> {
            int idx = table.getSelectedRow();
            if (myReservationList.isEmpty() || idx == -1) {
                JOptionPane.showMessageDialog(this, "취소할 예약이 없습니다.");
                return;
            }
            StudentApprovedModel selectedModel = myReservationList.get(idx);
            boolean result = StudentRejectedController.cancelReservation(
                    reservationFile,
                    selectedModel.getRoomNumber(),
                    selectedModel.getName(),
                    selectedModel.getRole(),
                    selectedModel.getTimeSlots(),
                    selectedModel.getDay(),
                    selectedModel.getRoomType()
            );
            if (result) {
                tableModel.setValueAt("취소", idx, 4);
                JOptionPane.showMessageDialog(this, "예약이 취소되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패 - 이미 취소된 예약이거나 잘못된 접근입니다.");
            }
        });

        // 닫기 버튼 이벤트
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    // timeSlots null 안전 처리용
    private String safeJoin(List<String> slotList, String sep) {
        if (slotList == null) return "";
        return String.join(sep, slotList);
    }
}