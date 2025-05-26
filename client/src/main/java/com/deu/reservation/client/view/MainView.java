package com.deu.reservation.client.view;

import com.deu.reservation.client.model.User;
import com.deu.reservation.client.service.ReservationService;
import com.deu.reservation.client.ReservationClient;
import com.deu.reservation.client.view.login.LoginView;

import javax.swing.*;
import java.awt.*;

public abstract class MainView extends JFrame {
    protected final User currentUser;
    protected final ReservationService reservationService;
    protected final ReservationClient reservationClient;
    protected JPanel mainPanel;

    public MainView(User user, ReservationService reservationService, ReservationClient reservationClient) {
        this.currentUser = user;
        this.reservationService = reservationService;
        this.reservationClient = reservationClient;
        initializeUI();
    }

    private void initializeUI() {
        setTitle(getTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // 상단 메뉴바
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // 메인 컨텐츠
        initializeContent();
    }

    public abstract String getTitle();
    public abstract void initializeContent();

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 예약 메뉴
        JMenu reservationMenu = new JMenu("예약");
        JMenuItem newReservationItem = new JMenuItem("새 예약");
        newReservationItem.addActionListener(e -> showNewReservationDialog());
        reservationMenu.add(newReservationItem);

        JMenuItem myReservationsItem = new JMenuItem("내 예약");
        myReservationsItem.addActionListener(e -> showMyReservations());
        reservationMenu.add(myReservationsItem);

        if (canApproveReservations()) {
            JMenuItem pendingReservationsItem = new JMenuItem("승인 대기 예약");
            pendingReservationsItem.addActionListener(e -> showPendingReservations());
            reservationMenu.add(pendingReservationsItem);
        }

        menuBar.add(reservationMenu);

        // 계정 메뉴
        JMenu accountMenu = new JMenu("계정");
        JMenuItem logoutItem = new JMenuItem("로그아웃");
        logoutItem.addActionListener(e -> handleLogout());
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        return menuBar;
    }

    protected abstract void showNewReservationDialog();
    protected abstract void showMyReservations();
    protected abstract void showPendingReservations();
    protected abstract boolean canApproveReservations();

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "로그아웃 하시겠습니까?",
            "로그아웃",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            // 현재 창 닫기
            dispose();
            
            // 로그인 화면으로 돌아가기
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView(reservationClient);
                loginView.setVisible(true);
            });
        }
    }
} 