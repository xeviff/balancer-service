package com.npaw.service.balancer.servlets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import com.npaw.service.balancer.config.ConfigManager;
import com.npaw.service.balancer.model.DeviceConfiguration;

class ServicioTest {
	
	DecimalFormat df = new DecimalFormat("###.##");
	
	static ConfigManager configM;

	@BeforeAll
	static void init() throws Exception {
		configM = ConfigManager.getInstance();
	}
	
	@RepeatedTest(10)
	@DisplayName("Load Balance Test (xTimes) >> multiple calls, 1% margin error allowed")
	void loadBalanceTest1 () throws Exception {
		//given
		int callN = 1000000;
		byte cluster1LoadDef = 30; 
		byte cluster2LoadDef = 70;
		byte errMrgn = 1; //%
		String account = "clienteA";
		String device = "XBox";
		String cluster1 = "clusterA.com";
		String cluster2 = "clusterB.com";
		DeviceConfiguration deviceInfo = configM.getDevice(account, device);
		
		//when
		String[] balancedResult = new String[callN];
		for (int i=0 ; i<callN ; i++) {
			String cluster = deviceInfo.getBalancing().nextRequest();
			balancedResult[i]=cluster;
		}
		
		//then
		List<String> resultList = Arrays.asList(balancedResult);
		int cluster1Count = Collections.frequency(resultList, cluster1);
		int cluster2Count = Collections.frequency(resultList, cluster2);
		float cluster1Balance = calcPer100(cluster1Count,callN);
		float cluster2Balance = calcPer100(cluster2Count,callN);
		String headerLog = MessageFormat.format("Balancing test made for {2} calls, account {0} and device {1}.", 
				account, device, callN);
		System.out.println();
		System.out.println(headerLog);
		System.out.println("Balanced to cluster1Balance: "+printPercent(cluster1Balance));
		System.out.println("Balanced to cluster2Balance: "+printPercent(cluster2Balance));
		System.out.println("Error margin: "+printPercent(cluster1Balance-(float)cluster1LoadDef));
//		System.out.println("Error margin 2: "+printPercent(cluster2Balance-(float)cluster2LoadDef));
		
		assertAll(
				() -> assertTrue(isValueInAllowedErrMargin(cluster1Balance, cluster1LoadDef, errMrgn)),
				() -> assertTrue(isValueInAllowedErrMargin(cluster2Balance, cluster2LoadDef, errMrgn))
				);
	}
	
	private float calcPer100 (int amount, int total) {
		float perc = amount*(float)100/(float)total;
		return perc;
	}
	
	private boolean isValueInAllowedErrMargin (float value, byte defined, byte margin) {
		float mgn = (float)margin;
		float def = (float)defined;
		return value >= def-mgn && value <= def+mgn;
	}
	
	private String printPercent (float value) {
		return df.format(value)+" %";
	}
	
}
