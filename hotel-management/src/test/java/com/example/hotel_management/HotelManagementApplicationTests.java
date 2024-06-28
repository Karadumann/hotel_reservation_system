package com.example.hotel_management;

import com.example.hotel_management.model.User;
import com.example.hotel_management.model.UserRole;
import com.example.hotel_management.repository.UserRepository;
import com.example.hotel_management.repository.UserRoleRepository;
import com.example.hotel_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class HotelManagementApplicationTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserRoleRepository userRoleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	@Autowired
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testCreateOwner() {
		UserRole role = new UserRole();
		role.setRoleName("ROLE_OWNER");

		when(userRoleRepository.findByRoleName("ROLE_OWNER")).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

		User user = userService.createOwner("testUser", "password", null, "Test", "User", "1234567890", "Test Address");

		assertNotNull(user);
		assertEquals("ROLE_OWNER", user.getRole().getRoleName());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testFindByUsername() {
		User user = new User();
		user.setUsername("testUser");
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

		User foundUser = userService.findByUsername("testUser");

		assertNotNull(foundUser);
		assertEquals("testUser", foundUser.getUsername());
	}

	@Test
	void testSetUserActiveStatus() {
		User user = new User();
		user.setId(1L);
		user.setActive(true);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		userService.setUserActiveStatus(1L, false);

		assertFalse(user.isActive());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testCreateManager() {
		UserRole role = new UserRole();
		role.setRoleName("ROLE_MANAGER");

		when(userRoleRepository.findByRoleName("ROLE_MANAGER")).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

		User user = userService.createManager("managerUser", "password", null, "Manager", "User", "0987654321", "Manager Address");

		assertNotNull(user);
		assertEquals("ROLE_MANAGER", user.getRole().getRoleName());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testCreateReceptionist() {
		UserRole role = new UserRole();
		role.setRoleName("ROLE_RECEPTIONIST");

		when(userRoleRepository.findByRoleName("ROLE_RECEPTIONIST")).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

		User user = userService.createReceptionist("receptionistUser", "password", null, "Receptionist", "User", "1231231234", "Receptionist Address");

		assertNotNull(user);
		assertEquals("ROLE_RECEPTIONIST", user.getRole().getRoleName());
		verify(userRepository, times(1)).save(any(User.class));
	}
}
