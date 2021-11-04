package com.npaw.service.balancer.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Esta clase contiene el algoritmo de balanceo de carga. Este es por localización de número pseudoaleatorio,
 * teniendo en cuenta de que todas las posiciones tienen la misma probabilidad y así poder cortar por el porcentage deseado.
 * Se ha desarrollado permitiendo que sean tantos clústers como se quiera. 
 * @author Xavier
 *
 */
public class ClusterBalancing {
	
	//Catálogo de los clústers
	private HashMap<String,Byte> clusters = new HashMap<>();
		
	/**
	 * Se añade un cluster a la lista
	 * @param clusterName
	 * @param carga
	 */
	public void addCluster (String clusterName, Byte carga) {		
		clusters.put(clusterName, carga);
	}
		
	/**
	 * Este es el método que dada la información de los clústers (nombres y cargas)
	 * determina para la Request en curso cual es el cluster que le toca, dando así la funcionalidad de balanceo.
	 * Por ejemplo si tenemos clusterA=70%-clusterB=30%, se genera un número aleatorio entre 0 y 99,
	 * si el número está entre 0 y 69 devolveremos clusterA, en cambio si está entre 70 y 99 el clusterB. 
	 * Suena un poco raro pero paradójicamente funciona, con un 1% de error para 2 clústers. 
	 * @return
	 * @throws Exception
	 */
	public String nextRequest () throws Exception {
		double randNumber = Math.random();
		double d = randNumber * 100;
		byte num = (byte)d;
		
		byte acumulable=0;
		String clusterName = null;
		Iterator<?> it = clusters.entrySet().iterator();
		while (it.hasNext()) {
			Entry<?, ?> e = (Entry<?, ?>) it.next();
			clusterName = (String) e.getKey();
			Byte carga = (Byte) e.getValue();
			if (carga!=0) {
				acumulable+=carga.byteValue();
				if (num<acumulable)
					return clusterName;
			}
		}
		throw new Exception("Código teóricamente inalcanzable");
	}
	
}
