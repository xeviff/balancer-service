package com.npaw.service.balancer.model;

/**
 * Contiene la información de un dispositivo
 * @author Xavier
 *
 */
public class DeviceConfiguration {
	
	//Atributos
	private String name;
	//private String pluginVersion;
	private int pingTime;
		//Balanceador de carga
	private ClusterBalancing balancing = new ClusterBalancing();
		
	/**
	 * Constructor que rellena los datos
	 * @param name
	 * @param pluginVersion
	 * @param pingTime
	 * @param clusters Lista de clusters definidos
	 * @param balancing formato 70/30 (porcentajes separados por "/")
	 */
	public DeviceConfiguration(String name, String pluginVersion, int pingTime, String[] clusters, String balancing) {
		this.name = name;
		//this.pluginVersion = pluginVersion;
		this.pingTime = pingTime;
		//para cada cluster se asigna la carga y se guarda en el balanceador de carga (ClusterBalancing)
		String[] balances = balancing.split("/");
		for (int i=0; i<clusters.length ; i++) 
			this.balancing.addCluster(clusters[i], Byte.valueOf(balances[i]));
	}
	
	public ClusterBalancing getBalancing () {
		return balancing;
	}
	public String getName() {
		return name;
	}
	public int getPingTime() {
		return pingTime;
	}	
}
