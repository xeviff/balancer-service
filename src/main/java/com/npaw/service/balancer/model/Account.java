package com.npaw.service.balancer.model;

import java.util.HashMap;

/**
 * Clase de modelo para las cuentas de cliente
 * @author Xavier
 *
 */
public class Account {

	//Los dispositivos que tiene asociados
	private HashMap<String,DeviceConfiguration> devicesInfo;
	
	public Account () {
		devicesInfo = new HashMap<String, DeviceConfiguration>();
	}
			
	/**
	 * Añade un dispositivo asociado
	 * @param config
	 */
	public void addDeviceConfig (DeviceConfiguration config) {
		devicesInfo.put(config.getName(), config);
	}
	
	/**
	 * Pasado por parámetro el nombre de un dispositivo, indica si lo tiene asociado
	 * @param deviceName
	 * @return
	 */
	public boolean hasDevice (String deviceName) {
		return devicesInfo.containsKey(deviceName);
	}
	
	/**
	 * Devuelve la información de uno de sus dispositivos en un objeto de tipo DeviceConfiguration
	 * @param device
	 * @return
	 */
	public DeviceConfiguration getDeviceInfo (String device) {
		return devicesInfo.get(device);
	}
}
