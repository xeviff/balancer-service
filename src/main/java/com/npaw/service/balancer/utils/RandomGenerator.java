package com.npaw.service.balancer.utils;

import java.util.Date;
import java.util.Random;

/**
 * Clase que genera el view ID, utiliza la fecha actual en milisegundos para no duplicar resultados.
 * @author Xavier
 *
 */
public class RandomGenerator {

	private static Random random = new Random((new Date()).getTime());

	/**
	 * Devuelve un string aleatorio utilizando la fecha como semilla para el generador del aleatorio.
	 * @param length
	 * @return
	 */
	public static String generateRandomString(int length) {
		char[] values = {'a','b','c','d','e','f','g','h','i','j',
				'k','l','m','n','o','p','q','r','s','t',
				'u','v','w','x','y','z','0','1','2','3',
				'4','5','6','7','8','9'};

		String out = "";

		for (int i=0;i<length;i++) {
			int idx=random.nextInt(values.length);
			out += values[idx];
		}

		return out;
	}
}