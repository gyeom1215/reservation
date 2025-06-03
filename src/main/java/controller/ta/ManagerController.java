/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import model.ta.ReservationModel;
import view.ta.ManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManagerController {
    private ReservationModel model;
    private ManagerFrame view;

    public ManagerController(ReservationModel model, ManagerFrame view) {
        this.model = model;
        this.view = view;

        loadUsersToTable();   // 초기 사용자 목록 로딩
        // ❌ 버튼 이벤트는 더 이상 여기서 안 함 (View에서 직접 호출)
    }

    // ✅ View에서 직접 호출하는 메서드로 변경
    public void deleteSelectedUser(int rowIndex) {
        JTable table = view.getUserTable();

        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(view, "삭제할 계정을 선택하세요.");
            return;
        }

        String userId = table.getValueAt(rowIndex, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "정말로 이 계정을 삭제하시겠습니까?",
                "계정 삭제 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean result = model.deleteUser(userId);
            if (result) {
                JOptionPane.showMessageDialog(view, "계정이 삭제되었습니다.");
                loadUsersToTable(); // 삭제 후 새로고침
            } else {
                JOptionPane.showMessageDialog(view, "계정 삭제에 실패했습니다.");
            }
        }
    }

    public void loadUsersToTable() {
        List<String[]> users = model.loadUsers();
        DefaultTableModel model = (DefaultTableModel) view.getUserTable().getModel();
        model.setRowCount(0); // 기존 행 제거

        for (String[] row : users) {
            model.addRow(row);
        }
    }
}