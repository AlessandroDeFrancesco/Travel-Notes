package server.gateway;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Initializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Avvio server Mobidev.");
	}
		
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
			/*
			 * Metodo non utilizzato
			 */
	}


}