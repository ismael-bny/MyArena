package com.example.myarena.tests;

import com.example.myarena.facade.SessionFacade;
import com.example.myarena.facade.UserSession;
import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationTest {

    private SessionFacade sessionFacade;
    private static final String TEST_NAME = "Test User";
    private static final String TEST_EMAIL = "test_auth_" + System.currentTimeMillis() + "@myarena.com";
    private static final String TEST_PWD = "password123";

    @BeforeEach
    void setUp() {
        sessionFacade = SessionFacade.getInstance();
        sessionFacade.logout(); // Ensure a clean session for each test
    }

    @Test
    @Order(1)
    @DisplayName("Should fail registration with invalid inputs")
    void testRegisterInvalidInputs() {
        // Test short name
        assertEquals("Name must be at least 2 characters",
                sessionFacade.register("A", TEST_EMAIL, TEST_PWD, TEST_PWD, "0600000000"));

        // Test invalid email format
        assertEquals("Invalid email format",
                sessionFacade.register(TEST_NAME, "not-an-email", TEST_PWD, TEST_PWD, "0600000000"));

        // Test short password
        assertEquals("Password must be at least 6 characters",
                sessionFacade.register(TEST_NAME, TEST_EMAIL, "123", "123", "0600000000"));

        // Test password mismatch
        assertEquals("Passwords do not match",
                sessionFacade.register(TEST_NAME, TEST_EMAIL, TEST_PWD, "wrong_confirm", "0600000000"));
    }

    @Test
    @Order(2)
    @DisplayName("Should successfully register a new user")
    void testRegisterSuccess() {
        String result = sessionFacade.register(TEST_NAME, TEST_EMAIL, TEST_PWD, TEST_PWD, "0600000000");

        // register returns null on success
        assertNull(result, "Registration should return null on success");
    }

    @Test
    @Order(3)
    @DisplayName("Should fail registration with an existing email")
    void testRegisterDuplicateEmail() {
        // Attempting to register the same email used in Order(2)
        String result = sessionFacade.register("Other Name", TEST_EMAIL, TEST_PWD, TEST_PWD, "0600000000");
        assertEquals("Email already exists. Please login instead.", result);
    }

    @Test
    @Order(4)
    @DisplayName("Should fail login with wrong credentials")
    void testLoginFailure() {
        // Wrong password
        boolean success = sessionFacade.login(TEST_EMAIL, "wrong_password");
        assertFalse(success, "Login should fail with incorrect password");
        assertNull(sessionFacade.getCurrentUser(), "No user should be in session after failed login");

        // Non-existent email
        success = sessionFacade.login("non_existent@myarena.com", TEST_PWD);
        assertFalse(success, "Login should fail with non-existent email");
    }

    @Test
    @Order(5)
    @DisplayName("Should successfully login and manage session")
    void testLoginAndSessionSuccess() {
        boolean success = sessionFacade.login(TEST_EMAIL, TEST_PWD);

        assertTrue(success, "Login should succeed with correct credentials");

        User currentUser = sessionFacade.getCurrentUser();
        assertNotNull(currentUser, "User should be present in session");
        assertEquals(TEST_EMAIL, currentUser.getEmail());
        assertEquals(UserRole.CLIENT, currentUser.getRole(), "New users should default to CLIENT role");
        assertTrue(sessionFacade.isUserLoggedIn());
    }

    @Test
    @Order(6)
    @DisplayName("Should clear session on logout")
    void testLogout() {
        // Login first
        sessionFacade.login(TEST_EMAIL, TEST_PWD);
        assertTrue(sessionFacade.isUserLoggedIn());

        // Perform logout
        sessionFacade.logout();

        assertFalse(sessionFacade.isUserLoggedIn());
        assertNull(sessionFacade.getCurrentUser(), "Session should be empty after logout");
    }
}