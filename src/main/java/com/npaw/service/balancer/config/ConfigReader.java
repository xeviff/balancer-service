package com.npaw.service.balancer.config;

import java.util.HashMap;

/**
 * 
 * @author Xavier
 *
 */
public class ConfigReader {
	
	//	Atributos
	
	//clase que nos leer� las propiedades
	private ReadPropertyConfigurator propConf;
	
	//datos recogidos del fichero
	public String[] clusters;
	public HashMap<String, String> clientsConfig;
	public int time2Refresh;
	
	//constructor que ejecuta la recuperaci�n de datos
	public ConfigReader () throws Exception {
		propConf = new ReadPropertyConfigurator();
		getConfig();
	}
	
	/**
	 * Se encarga de recuperar del fichero los datos clasificadamente 
	 * y guardarlo en las variables que van a ser utilizadas posteriormente
	 */
	private void getConfig () {
		//coge la configuraci�n de los clientes (client_[nombre del cliente]=[informaci�n de dispositivos]) 
		clientsConfig = propConf.getListPropertyType("client", true);
		//coge la lista de los cl�sters (puede ser m�s de dos), en formato clusterX.com
		HashMap<String, String> clustersProp = propConf.getListPropertyType("cluster", false);
		clusters = clustersProp.keySet().toArray(new String[clustersProp.size()]);
		//El tiempo en milisegundos que transcurrir� entre un refresco de cach� y el siguiente
		time2Refresh = Integer.valueOf(propConf.getProperty("time2Refresh"));
	}
}