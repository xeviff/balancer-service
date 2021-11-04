package com.npaw.service.balancer.config;
 
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
 
/**
 * 
 * @author Xavier
 *
 */
public class ReadPropertyConfigurator {
 
   private Properties prop;
 
   /**
    * El constructor abre el fichero y carga las propiedades
    * @throws Exception
    */
    public ReadPropertyConfigurator() throws Exception {
        prop = new Properties();
        try{
            //Load the properties file
            InputStream is = getClass().getResourceAsStream("/config.properties");
            prop.load(is);
            is.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
 
 
    /**
     * Devuelve una propiedad dada su clave
     * @param key
     * @return
     */
    public String getProperty(String key) {
    	String propValue = prop.getProperty(key);
    	System.out.println(key+"="+propValue);
        return propValue;
    }
 
    /**
     * Devuelve todas las propiedades que comiencen por un String en concreto
     * @param type
     * @param getCodeFromName hace que coja la key del mismo nombre (seguido de un "_")
     * @return 
     */
    public HashMap<String, String> getListPropertyType (String type, boolean getCodeFromName) {
        HashMap<String, String> selectedProp = new HashMap<String, String>();
        Enumeration<?> enmProp = prop.propertyNames();
        while (enmProp.hasMoreElements()) {
            String element = (String) enmProp.nextElement();
            try {
                if (element.startsWith(type)) {
                    String value = prop.getProperty(element);
                    if (getCodeFromName&&element.length()>type.length())
                        element = element.substring(type.length()+1);
                    selectedProp.put(element,value);
                    System.out.println(element+"="+value);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error recuperant l'element "+element);
            }
        }
        return selectedProp;
    }
 
}
 