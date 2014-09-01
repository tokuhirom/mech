package me.geso.mech;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Tester class for Servlets. This class runs httpd by jetty. Your servlet runs
 * on jetty.
 * 
 * @author tokuhirom
 *
 */
public class MechJettyServlet extends Mech implements AutoCloseable {
	private Server server;

	public MechJettyServlet(Class<? extends Servlet> servlet) {
		ServletHolder servletHolder = new ServletHolder(servlet);
		this.initialize(servletHolder);
	}

	public MechJettyServlet(Servlet servlet) {
		ServletHolder servletHolder = new ServletHolder(servlet);
		this.initialize(servletHolder);
	}

	private void initialize(ServletHolder servletHolder) {
		try {
			this.server = createServer(servletHolder);
			this.server.start();
			ServerConnector connector = (ServerConnector) server
					.getConnectors()[0];
			int port = connector.getLocalPort();
			this.setBaseURL("http://127.0.0.1:" + port);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Server createServer(ServletHolder servletHolder) {
		int port = 0;
		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(
				server,
				"/",
				ServletContextHandler.SESSIONS
				);
		context.addServlet(servletHolder, "/*");
		server.setStopAtShutdown(true);
		return server;
	}

	@Override
	public void close() throws Exception {
		if (this.server != null) {
			this.server.stop();
		}
	}

}