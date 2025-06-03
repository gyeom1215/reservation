package model.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    
    @Test
    public void testUserCreation() {
        User user = new User("test123", "password123", "테스트사용자", "컴퓨터공학과", "student");
        
        assertEquals("test123", user.getId());
        assertEquals("password123", user.getPassword());
        assertEquals("테스트사용자", user.getName());
        assertEquals("컴퓨터공학과", user.getDepartment());
        assertEquals("student", user.getRole());
    }
    
    @Test
    public void testUserRoleValidation() {
        User student = new User("student1", "pwd1", "학생1", "컴퓨터공학과", "student");
        User professor = new User("prof1", "pwd2", "교수1", "컴퓨터공학과", "professor");
        User ta = new User("ta1", "pwd3", "조교1", "컴퓨터공학과", "ta");
        
        assertTrue(student.getRole().equals("student") || 
                  professor.getRole().equals("professor") || 
                  ta.getRole().equals("ta"));
    }
    
    @Test
    public void testUserUpdate() {
        User user = new User("test123", "password123", "테스트사용자", "컴퓨터공학과", "student");
        
        user.setPassword("newpassword");
        user.setName("새이름");
        user.setDepartment("전자공학과");
        
        assertEquals("newpassword", user.getPassword());
        assertEquals("새이름", user.getName());
        assertEquals("전자공학과", user.getDepartment());
    }
} 