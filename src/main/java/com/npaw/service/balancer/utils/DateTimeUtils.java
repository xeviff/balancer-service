package com.npaw.service.balancer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilidades para el tratamiento de milisegundos
 * @author Xavier
 *
 */
public class DateTimeUtils {
	
	private static SimpleDateFormat readableMilisecondsFormat = new SimpleDateFormat("kk:mm:ss S");
	
	public static String getMilisReadable_Now (){
		return readableMilisecondsFormat.format(new Date());
	}
	
	public static String getMilisReadable (long milis){
		return readableMilisecondsFormat.format(new Date(milis));
	} 
	
}
