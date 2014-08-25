package me.geso.testmech;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class TestMechTest {

	public static class Servlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		public void service(ServletRequest sreq, ServletResponse sres)
				throws ServletException, IOException {
			try {
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
				case "/readJson":
					res.getWriter().write("{\"name\":\"fuga\"}");
					break;
				case "/readJsonUTF8":
					String json = "{\"name\":\"田中\"}";
					byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
					res.getOutputStream().write(jsonBytes);;
					break;
				case "/postForm": {
					String name = req.getParameter("name");
					res.setCharacterEncoding("UTF-8");
					res.getWriter().write(name);
					break;
				}
				case "/postMultipart": {
					req.setCharacterEncoding("UTF-8");
					res.setCharacterEncoding("UTF-8");
					FileItemFactory factory = new DiskFileItemFactory();
					ServletFileUpload servletFileUpload = new ServletFileUpload(
							factory);
					Map<String, List<FileItem>> map = servletFileUpload
							.parseParameterMap(req);
					String name = map.get("name").get(0).getString();
					List<FileItem> files = map.get("file");
					FileItem file = files.get(0);
					res.getWriter().write(name + "XXX" + file.getName());
					break;
				}
				case "/json": {
					ServletInputStream inputStream = req.getInputStream();
					try (java.util.Scanner s = new java.util.Scanner(
							inputStream)) {
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
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class Form {
		String name;

		// dummy
		public Form() {
		}

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
	public void testJsonPathQuery() {
		Form form = new Form("hoge");
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.postJSON("/json?foo=bar", form).execute();
		res.assertSuccess();
		res.assertContentEquals("+++{\"name\":\"hoge\"}+++");
	}

	@Test
	public void testReadJsonWithTypeReference() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.get("/readJson").execute();
		res.assertSuccess();
		Form form = res.readJSON(new TypeReference<Form>() {
		});
		assertEquals(form.getName(), "fuga");
	}

	@Test
	public void testReadJsonUTF8() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.get("/readJsonUTF8").execute();
		res.assertSuccess();
		Form form = res.readJSON(new TypeReference<Form>() {
		});
		assertEquals(form.getName(), "田中");
	}

	@Test
	public void testQuery() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.get("/query?x=y").execute();
		res.assertSuccess();
		res.assertContentEquals("++x++y");
	}

	@Test
	public void testPostForm() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.post("/postForm").param("name", "pp太郎")
				.execute();
		res.assertSuccess();
		res.assertContentEquals("pp太郎");
	}

	@Test
	public void testPostMultipart() {
		TestMechJettyServlet mech = new TestMechJettyServlet(Servlet.class);
		TestMechResponse res = mech.postMultipart("/postMultipart")
				.param("name", "pp太郎").file("file", new File("pom.xml"))
				.execute();
		res.assertSuccess();
		res.assertContentEquals("pp太郎XXXpom.xml");
	}

}
