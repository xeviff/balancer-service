package com.npaw.service.balancer.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Esta clase contiene el algoritmo de balanceo de carga. Este es por localizaci�n de n�mero pseudoaleatorio,
 * teniendo en cuenta de que todas las posiciones tienen la misma probabilidad y as� poder cortar por el porcentage deseado.
 * Se ha desarrollado permitiendo que sean tantos cl�sters como se quiera. 
 * @author Xavier
 *
 */
public class ClusterBalancing {
	
	//Cat�logo de los cl�sters
	private HashMap<String,Byte> clusters = new HashMap<>();
		
	/**
	 * Se a�ade un cluster a la lista
	 * @param clusterName
	 * @param carga
	 */
	public void addCluster (String clusterName, Byte carga) {		
		clusters.put(clusterName, carga);
	}
		
	/**
	 * Este es el m�todo que dada la informaci�n de los cl�sters (nombres y cargas)
	 * determina para la Request en curso cual es el cluster que le toca, dando as� la funcionalidad de balanceo.
	 * Por ejemplo si tenemos clusterA=70%-clusterB=30%, se genera un n�mero aleatorio entre 0 y 99,
	 * si el n�mero est� entre 0 y 69 devolveremos clusterA, en cambio si est� entre 70 y 99 el clusterB. 
	 * Suena un poco raro pero parad�jicamente funciona, con un 1% de error para 2 cl�sters. 
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
		throw new Exception("C�digo te�ricamente inalcanzable");
	}
	
}
