package com.npaw.service.balancer.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigManagerTest {
	
	static ConfigManager configM;

	@BeforeAll
	static void init() throws Exception {
		configM = ConfigManager.getInstance();
	}
	
	/**
	 * Account check
	 */
	@Test
	@DisplayName("Account Test >>> Exist check")
	void should_ReturnTrue_WhenAccountExist () {
		//given
		String account = "clienteA";
		
		//when
		boolean existAcc = configM.existsAccount(account);
		
		//then
		assertTrue(existAcc);
	}
	
	@Test
	@DisplayName("Account Test >>> Not exist check")
	void should_ReturnFalse_WhenAccountNotExist () {
		//given
		String account = "xavier";
		
		//when
		boolean existAcc = configM.existsAccount(account);
		
		//then
		assertFalse(existAcc);
	}
	
	/**
	 * Device check
	 */
	@Test
	@DisplayName("Device Test >>> Device pertains to an account")
	void should_ReturnTrue_WhenDeviceMatchAccount () {
		//given
		String account = "clienteA";
		String device = "XBox";
		
		//when
		boolean match = configM.deviceAccepted4Account(account, device);
		
		//then
		assertTrue(match);
	}
	
	@Test
	@DisplayName("Device Test >>> Device NOT pertains to an account")
	void should_ReturnFalse_WhenDeviceNotMatchAccount () {
		//given
		String account = "clienteB";
		String device = "Panasonic";
		
		//when
		boolean match = configM.deviceAccepted4Account(account, device);
		
		//then
		assertFalse(match);
	}
	
}
