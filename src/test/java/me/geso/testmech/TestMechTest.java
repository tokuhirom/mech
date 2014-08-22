package me.geso.testmech;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class TestMechTest {

	public static class Servlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		public void service(ServletRequest sreq, ServletResponse sres)
				throws ServletException, IOException {
			HttpServletRequest req = (HttpServletRequest) sreq;
			HttpServletResponse res = (HttpServletResponse) sres;
			System.out.println(req.getPathInfo());
			switch (req.getPathInfo()) {
			case "/":
				res.setContentType("text/plain; charset=UTF-8");
				res.getWriter().write("heheh");
				break;
			case "/hogehoge":
				res.setContentType("iyan");
				res.getWriter().write("hogehoge");
				break;
			case "/query":
				res.getWriter().write("++x++" + req.getParameter("x"));
				break;
			case "/json": {
				ServletInputStream inputStream = req.getInputStream();
				try (java.util.Scanner s = new java.util.Scanner(inputStream)) {
					s.useDelimiter("\\A");
					String buf = s.hasNext() ? s.next() : "";
					System.out.println(buf);

					res.setContentType("iyan");
					res.getWriter().write("+++" + buf + "+++");
				}
				break;
			}
			default:
				res.setStatus(404);
				break;
			}
		}
	}

	public static class Form {
		String name;

		public Form(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Test
	public void testRoot() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.get("/").execute();
		res.assertSuccess();
		res.assertContentTypeEquals("text/plain; charset=UTF-8");
		res.assertContentContains("heheh");
	}

	@Test
	public void testHoge() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.get("/hogehoge").execute();
		res.assertSuccess();
		res.assertStatusEquals(200);
		res.assertContentTypeContains("iyan");
	}

	@Test
	public void testJson() {
		Form form = new Form("hoge");
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.postJSON("/json", form).execute();
		res.assertSuccess();
		res.assertContentEquals("+++{\"name\":\"hoge\"}+++");
	}

	@Test
	public void testQuery() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.get("/query?x=y").execute();
		res.assertSuccess();
		res.assertContentEquals("++x++y");
	}

}
