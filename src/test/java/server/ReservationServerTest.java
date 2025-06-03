package server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import server.ReservationServer;
import static org.junit.jupiter.api.Assertions.*;

class ReservationServerTest {
    private ReservationServer reservationServer;

    @BeforeEach
    void setUp() {
        reservationServer = new ReservationServer();
    }

    @Test
    @DisplayName("서버 초기화 테스트")
    void testServerInitialization() {
        assertNotNull(reservationServer, "서버 객체가 정상적으로 생성되어야 합니다");
    }

    // 여기에 추가적인 서버 기능 테스트 메서드들을 구현
} 