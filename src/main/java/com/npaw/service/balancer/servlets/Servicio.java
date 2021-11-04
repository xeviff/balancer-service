package com.npaw.service.balancer.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.npaw.service.balancer.config.ConfigManager;
import com.npaw.service.balancer.model.DeviceConfiguration;
import com.npaw.service.balancer.utils.DateTimeUtils;
import com.npaw.service.balancer.utils.RandomGenerator;

public class Servicio extends HttpServlet {
	
	//	Atributos
	private static final long serialVersionUID = -4204554333816364624L;
	private static final String STATE_FATALERR = "fe";
	private static final String STATE_FAILED = "fail";
	private static final String STATE_SUCCESS = "ok";
	
		//Objeto de configuración (apuntará a una instancia unica y estática)
	private ConfigManager confManager;	
	
	@Override
	/**
	 * Este método se ejecuta una sola vez para inicializar la cache. 
	 * Después de esto, todos los threads de este servlet van a reaprovechar la misma información cacheada (hasta que se refresque).
	 * Se ha configurado el web.xml para que se ejecute en arrancar el servidor.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		getServletContext().setAttribute(STATE_FATALERR, false);
		try {
			//Se inicializa la instancia que contendrá la caché de la configuración del servicio
			confManager = ConfigManager.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			//Si ha habido error en inicializar, lo anotamos
			getServletContext().setAttribute(STATE_FATALERR, true);
		}
	}

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//se inicializan algunas variables
		long initialTime = System.currentTimeMillis();
		String view = RandomGenerator.generateRandomString(16);
		System.out.println(view+": Inicia en "+DateTimeUtils.getMilisReadable(initialTime));
		
		//se haberse producido algun error al recuperar la configuración, no se procede
		boolean fatalError = (boolean) getServletContext().getAttribute(STATE_FATALERR);
		if (!fatalError) {			
			//se recuperan los parámetros
			String accountCode = req.getParameter("accountCode");
			String targetDevice = req.getParameter("targetDevice");
			String forceRefresh = req.getParameter("forceRefresh");
			boolean refresh = forceRefresh!=null&&forceRefresh.equals("1");
			System.out.println(view+": Parametros="+accountCode+","+targetDevice+","+forceRefresh+" ");
			try {	
				//Antes de comprobar los datos, se mira si hay que refrescar (y se hace en caso afirmativo)
				if (refresh) confManager.refresh();
				else confManager.checkRefresh(); 
				//Se valida los parámetros, si alguno vital viene vacío se corta el proceso
				boolean parametrossVacios = accountCode==null||accountCode.isEmpty() || targetDevice==null||targetDevice.isEmpty();
				if (!parametrossVacios) {					
					//Entonces se contrasta con la configuración si existe la cuenta cliente y si tiene dicho dispositivo asociado
					if (!confManager.existsAccount(accountCode) || !confManager.deviceAccepted4Account(accountCode, targetDevice) )
						validationFailed(resp, initialTime, view);
					else {
						//De ser los parámetros correctos, se extrae la información, se calcula el viewId y el servidor a donde irá esta petición
						DeviceConfiguration deviceInfo = confManager.getDevice(accountCode, targetDevice);
						String cluster = deviceInfo.getBalancing().nextRequest();
						String ping = String.valueOf( deviceInfo.getPingTime() );						
						response(resp, STATE_SUCCESS, cluster, ping, view);
						long now = System.currentTimeMillis();
						long timeElapsed = now-initialTime;
						System.out.println(view+": Tiempo consumido por la petición: "+timeElapsed+" milisegundos. "
								+ "Empezó: "+DateTimeUtils.getMilisReadable(initialTime)+" y acabó en "+DateTimeUtils.getMilisReadable_Now());
					}
				} else 
					validationFailed(resp, initialTime, view);
				
			} catch (Exception e) {
				e.printStackTrace();
				fatalError(resp);
			}			
		} else
			fatalError(resp);
	}
	
	private void fatalError (HttpServletResponse resp ) throws IOException {
		response(resp, STATE_FATALERR, null, null, null);
	}
	
	private void validationFailed (HttpServletResponse resp, long initialTime, String view) throws IOException {
		System.out.println(view+": Validación fallida. Empezó: "+DateTimeUtils.getMilisReadable(initialTime));
		response(resp, STATE_FAILED, null, null, null);
	}
	
	private void response(HttpServletResponse resp, String msg, String cluster, String ping, String view)
			throws IOException {
		PrintWriter out = resp.getWriter();
		if (msg.equals(STATE_SUCCESS)) {
			out.println("<?xml version='1.0' encoding='UTF-8'?>");
			out.println("<q>");
			out.println("<h>"+cluster+"</h>");
			out.println("<pt>"+ping+"</pt>");
			out.println("<c>"+view+"</c>");
			out.println("</q>");
			out.println("</xml>");	
		} else if (msg.equals(STATE_FAILED)) {
			out.println("");
		} else if (msg.equals(STATE_FATALERR)) {
			out.println("<html>");
			out.println("<body>");
			out.println("<t1>Ha habido un error al recuperar los datos de inicialización</t1>");
			out.println("</body>");
			out.println("</html>");
		}
		out.close();
	}
}
