package controller;

import model.user.User;
import model.user.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import controller.login.AuthController;

public class AuthControllerTest {
    private AuthController authController;
    private UserDAO userDAO;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setup() {
        String testFilePath = tempDir.resolve("test_users.json").toString();
        userDAO = new UserDAO(testFilePath);
        authController = new AuthController(userDAO);
    }
    
    @Test
    public void testSuccessfulLogin() {
        // 테스트용 사용자 생성 및 저장
        User testUser = new User("testuser", "testpass", "테스트사용자", "컴퓨터공학과", "student");
        assertTrue(userDAO.saveUser(testUser), "사용자 저장 실패");
        
        // 로그인 테스트
        User loggedInUser = authController.login("testuser", "testpass");
        assertNotNull(loggedInUser, "로그인된 사용자가 null입니다");
        assertEquals("testuser", loggedInUser.getId(), "사용자 ID가 일치하지 않습니다");
        assertEquals("student", loggedInUser.getRole(), "사용자 역할이 일치하지 않습니다");
    }
    
    @Test
    public void testFailedLogin() {
        // 잘못된 비밀번호로 로그인 시도
        User loggedInUser = authController.login("testuser", "wrongpass");
        assertNull(loggedInUser, "잘못된 로그인이 성공했습니다");
    }
    
    @Test
    public void testSuccessfulRegistration() {
        User newUser = new User("newuser", "newpass", "신규사용자", "전자공학과", "student");
        assertTrue(authController.register(newUser), "사용자 등록 실패");
        
        // 등록된 사용자로 로그인 테스트
        User loggedInUser = authController.login("newuser", "newpass");
        assertNotNull(loggedInUser, "등록된 사용자로 로그인 실패");
        assertEquals("newuser", loggedInUser.getId(), "등록된 사용자 ID가 일치하지 않습니다");
    }
    
    @Test
    public void testDuplicateRegistration() {
        User user1 = new User("dupuser", "pass1", "사용자1", "컴퓨터공학과", "student");
        assertTrue(authController.register(user1), "첫 번째 사용자 등록 실패");
        
        // 동일한 ID로 다시 등록 시도
        User user2 = new User("dupuser", "pass2", "사용자2", "전자공학과", "student");
        assertFalse(authController.register(user2), "중복 사용자 등록이 성공했습니다");
    }
    
    @Test
    public void testUserUpdate() {
        // 사용자 생성
        User user = new User("updateuser", "oldpass", "기존이름", "컴퓨터공학과", "student");
        assertTrue(authController.register(user), "사용자 등록 실패");
        
        // 사용자 정보 업데이트
        user.setName("새이름");
        user.setPassword("newpass");
        assertTrue(authController.updateUser(user), "사용자 정보 업데이트 실패");
        
        // 업데이트된 정보로 로그인
        User updatedUser = authController.login("updateuser", "newpass");
        assertNotNull(updatedUser, "업데이트된 정보로 로그인 실패");
        assertEquals("새이름", updatedUser.getName(), "업데이트된 이름이 일치하지 않습니다");
    }
    
    @Test
    public void testUserDeletion() {
        // 사용자 생성
        User user = new User("deleteuser", "pass", "삭제될사용자", "컴퓨터공학과", "student");
        assertTrue(authController.register(user), "사용자 등록 실패");
        
        // 사용자 삭제
        assertTrue(authController.deleteUser("deleteuser"), "사용자 삭제 실패");
        
        // 삭제된 사용자로 로그인 시도
        assertNull(authController.login("deleteuser", "pass"), "삭제된 사용자로 로그인이 성공했습니다");
    }
} 