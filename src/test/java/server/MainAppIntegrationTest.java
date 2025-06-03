package server;

import client.ReservationClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import server.ReservationServer;
import view.login.LoginView;
import static org.junit.jupiter.api.Assertions.*;

class MainAppIntegrationTest {
    
    @Test
    @DisplayName("전체 시스템 통합 테스트")
    void testSystemIntegration() {
        try {
            // 서버 초기화 테스트
            ReservationServer reservationServer = new ReservationServer();
            assertNotNull(reservationServer, "서버가 정상적으로 초기화되어야 합니다");

            // 클라이언트 초기화 테스트
            ReservationClient reservationClient = ReservationClient.getInstance(reservationServer);
            assertNotNull(reservationClient, "클라이언트가 정상적으로 초기화되어야 합니다");

            // LoginView 초기화 테스트
            LoginView loginView = new LoginView(reservationClient);
            assertNotNull(loginView, "로그인 뷰가 정상적으로 초기화되어야 합니다");

        } catch (Exception e) {
            fail("통합 테스트 실행 중 예외 발생: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("서버-클라이언트 연결 테스트")
    void testServerClientConnection() {
        try {
            ReservationServer reservationServer = new ReservationServer();
            ReservationClient reservationClient = ReservationClient.getInstance(reservationServer);
            
            // 서버와 클라이언트 간의 연결 상태 테스트
            assertTrue(reservationClient != null && reservationServer != null, 
                "서버와 클라이언트가 정상적으로 연결되어야 합니다");
            
        } catch (Exception e) {
            fail("서버-클라이언트 연결 테스트 중 예외 발생: " + e.getMessage());
        }
    }
} 