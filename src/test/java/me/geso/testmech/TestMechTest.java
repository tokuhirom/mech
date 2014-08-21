package me.geso.testmech;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.util.ByteArrayBuffer;
import org.junit.Test;

public class TestMechTest {

	public static class Servlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		public void service(ServletRequest sreq, ServletResponse sres)
				throws ServletException, IOException {
			HttpServletRequest req = (HttpServletRequest) sreq;
			HttpServletResponse res = (HttpServletResponse) sres;
			switch (req.getPathInfo()) {
			case "/":
				res.setContentType("text/plain");
				res.getWriter().write("heheh");
				break;
			case "/hogehoge":
				res.setContentType("iyan");
				res.getWriter().write("hogehoge");
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
		TestMechServlet mech = new TestMechServlet(Servlet.class);
		TestMechResponse res = mech.get("/").execute();
		res.assertSuccess();
		res.assertContentContains("heheh");
	}

	@Test
	public void testHoge() {
		TestMechServlet mech = new TestMechServlet(Servlet.class);
		TestMechResponse res = mech.get("/hogehoge").execute();
		res.assertSuccess();
		res.assertStatusEquals(200);
		res.assertContentTypeContains("iyan");
	}

	@Test
	public void testJson() {
		Form form = new Form("hoge");
		TestMechServlet mech = new TestMechServlet(Servlet.class);
		TestMechResponse res = mech.postJSON("/json", form).execute();
		res.assertSuccess();
		res.assertContentEquals("+++{\"name\":\"hoge\"}+++");
	}

}
