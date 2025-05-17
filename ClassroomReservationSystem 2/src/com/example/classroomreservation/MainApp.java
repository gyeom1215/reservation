package com.example.classroomreservation;

import com.example.classroomreservation.ui.StudentReservationFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 실제 운영 시에는 로그인 창을 띄우거나, 프로그램 인자로 학생 ID를 받을 수 있습니다.
            // 여기서는 간단히 JOptionPane으로 학생 ID를 입력받습니다.
            String studentId = JOptionPane.showInputDialog(null, "학생 ID를 입력하세요:", "학생 로그인", JOptionPane.PLAIN_MESSAGE);

            if (studentId != null && !studentId.trim().isEmpty()) {
                new StudentReservationFrame(studentId.trim()).setVisible(true);
            } else {
                // 학생 ID 입력 없이도 사용 가능하도록 (ID 입력 필드 활성화)
                 // JOptionPane.showMessageDialog(null, "학생 ID가 입력되지 않아 프로그램을 종료합니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                 // System.exit(0);
                 new StudentReservationFrame().setVisible(true); // ID 없이 실행
            }
        });
    }
}