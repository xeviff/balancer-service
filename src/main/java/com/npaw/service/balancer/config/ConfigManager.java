package com.npaw.service.balancer.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.npaw.service.balancer.model.Account;
import com.npaw.service.balancer.model.DeviceConfiguration;
import com.npaw.service.balancer.utils.DateTimeUtils;

public class ConfigManager {
	
	//Singleton design-pattern
	//	Se fuerza que solo haya una instancia de la que se consultan y refrescan los datos de la configuraci�n
	private static ConfigManager uniqueInstance;
    private synchronized static void createInstance() throws Exception {
        if (uniqueInstance == null) uniqueInstance = new ConfigManager();
    }
    public static ConfigManager getInstance() throws Exception {
        if (uniqueInstance == null) createInstance();
        return uniqueInstance;
    }
    
	//*******************
	//Class functionality
    //*******************
    //	Atributos
	private ConfigReader reader;
	private HashMap<String, Account> accountsInfo = new HashMap<>();
	private long lastRetrieve;
	
	//Contructor
	private ConfigManager () throws Exception {
		getConfig();
	}
	
	/**
	 * M�todo que recupera toda la configuraci�n del servicio y la guarda en los atributos de esta clase
	 * @throws Exception
	 */
	private synchronized void getConfig () throws Exception {
		lastRetrieve = System.currentTimeMillis();
		System.out.println("getting config at "+DateTimeUtils.getMilisReadable(lastRetrieve)+".......");		
		reader = new ConfigReader();
		Iterator<?> it = reader.clientsConfig.entrySet().iterator();
		//para cada cuenta de cliente creamos un objeto Account y recuperamos los dispositivos asociados
		while (it.hasNext()) {
			Entry<?, ?> e = (Entry<?, ?>) it.next();
			String accountName = (String) e.getKey();
			String config = (String) e.getValue();
			
			Account account = new Account();			
			accountsInfo.put(accountName.toLowerCase(),account);
									
			String[] devicesInfo = config.toLowerCase().split(";");
			//para cada dispositivo creamos un objeto DeviceConfiguration y lo asociamos al objeto Account
			for (String deviceInfo: devicesInfo) {
				String[] info = deviceInfo.split("-");
				String deviceName = info[0];
				String pluguin = info[1];
				int ping = Integer.valueOf(info[2]);
				String balancing = info[3];
				DeviceConfiguration deviceConf = new DeviceConfiguration(deviceName,pluguin,ping,reader.clusters,balancing);
				account.addDeviceConfig(deviceConf);
			}
		}
		long timeElapsed = System.currentTimeMillis()-lastRetrieve;
		System.out.println("Tiempo consumido por la carga: "+timeElapsed+" milisegundos");
	}
	
	/**
	 * Comprueba si hay que refrescar la cache y en su caso lo hace
	 * @return
	 * @throws Exception
	 */
	public boolean checkRefresh () throws Exception {
		return refresh(false);
	}
	
	/**
	 * Refresca forzadamente la cache
	 * @throws Exception
	 */
	public void refresh () throws Exception {
		refresh(true);
	}
	
	/**
	 * M�todo que se encarga de refrescar la cache de la configuraci�n. Se puede hacer forzadamente por par�metro
	 * o sin� se va a refrescar cada cierto tiempo (el par�metro del tiempo est� definido en el fichero de configuraci�n).
	 * Se protege del acceso concurrente ya que dos procesos podr�an acceder exactamente al mismo milisegundo 
	 * duplicando(triplicando,...) as� el refresco de los datos. 	
	 * @param force par�metro que indica si se refresca forzadamente
	 * @return
	 * @throws Exception
	 */
	private synchronized boolean refresh (boolean force) throws Exception {
		long now = System.currentTimeMillis();
		if (force || (now - lastRetrieve)>reader.time2Refresh ) {
			if (!force)
				System.out.println("refreshing at "+DateTimeUtils.getMilisReadable_Now()+
						", cause the "+reader.time2Refresh/1000+" seconds configured has passed from the "+
						DateTimeUtils.getMilisReadable(lastRetrieve));
			else
				System.out.println("refreshed forced");
			getConfig(); 
			return true;
		}
		return false;
	}
	
	/**
	 * Comprueba si existe la cuenta de cliente proporcionada por par�metro
	 * @param account
	 * @return
	 */
	public boolean existsAccount (String account) {
		return accountsInfo.containsKey(account.toLowerCase());
	}
	
	/**
	 * Comprueba si la cuenta de cliente tiene asociado el dispositivo del par�metro 
	 * @param account
	 * @param device
	 * @return
	 */
	public boolean deviceAccepted4Account (String account, String device) {
		return accountsInfo.get(account.toLowerCase()).hasDevice(device.toLowerCase());
	}
	
	/**
	 * Devuelve la informaci�n del dispositivo dado su nombre y cuenta de cliente asociada
	 * @param accountCode
	 * @param deviceTarget
	 * @return
	 */
	public DeviceConfiguration getDevice (String accountCode, String deviceTarget) {
		return accountsInfo.get(accountCode.toLowerCase()).getDeviceInfo(deviceTarget.toLowerCase());
	}
	
}
