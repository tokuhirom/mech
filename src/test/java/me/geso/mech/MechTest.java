package me.geso.mech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.geso.mech.MechJettyServlet;
import me.geso.mech.MechResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class MechTest {

	@FunctionalInterface
	public static interface ServletCallback {
		public void service(HttpServletRequest sreq, HttpServletResponse sres)
				throws Exception;
	}

	public static class CallbackServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private final ServletCallback callback;

		public CallbackServlet(ServletCallback callback) {
			this.callback = callback;
		}

		public void service(ServletRequest sreq, ServletResponse sres)
				throws ServletException, IOException {
			HttpServletRequest req = (HttpServletRequest) sreq;
			HttpServletResponse res = (HttpServletResponse) sres;
			try {
				this.callback.service(req, res);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
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
	public void testRoot() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							res.setContentType("text/plain; charset=UTF-8");
							res.getWriter().write("heheh");
						}))) {
			MechResponse res = mech.get("/").execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentType().getMimeType(), "text/plain");
			assertEquals(res.getContentType().getCharset().displayName(), "UTF-8");
			assertTrue(res.getContentString().contains("heheh"));
		}
	}

	@Test
	public void testHoge() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							res.setContentType("application/x-iyan; charset=Shift_JIS");
							res.getWriter().write("hogehoge");
						}))) {
			MechResponse res = mech.get("/hogehoge").execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentType().getMimeType(), "application/x-iyan");
			assertEquals(res.getContentType().getCharset().displayName(), "Shift_JIS");
		}
	}

	@Test
	public void testJson() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							ServletInputStream inputStream = req
									.getInputStream();
							try (java.util.Scanner s = new java.util.Scanner(
									inputStream)) {
								s.useDelimiter("\\A");
								String buf = s.hasNext() ? s.next() : "";

								res.setContentType("iyan");
								res.getWriter().write("+++" + buf + "+++");
							}
						}))) {
			Form form = new Form("hoge");
			MechResponse res = mech.postJSON("/json", form).execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "+++{\n"
					+ "  \"name\" : \"hoge\"\n"
					+ "}+++");
		}
	}

	@Test
	public void testJsonPathQuery() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							ServletInputStream inputStream = req
									.getInputStream();
							try (java.util.Scanner s = new java.util.Scanner(
									inputStream)) {
								s.useDelimiter("\\A");
								String buf = s.hasNext() ? s.next() : "";

								res.setContentType("iyan");
								res.getWriter().write("+++" + buf + "+++");
							}
						}))) {
			Form form = new Form("hoge");
			MechResponse res = mech.postJSON("/json?foo=bar", form)
					.execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "+++{\n  \"name\" : \"hoge\"\n}+++");
		}
	}

	@Test
	public void testReadJsonWithTypeReference() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							res.getWriter().write("{\"name\":\"fuga\"}");
						}))) {
			MechResponse res = mech.get("/readJson").execute();
			assertEquals(res.getStatus(), 200);
			Form form = res.readJSON(new TypeReference<Form>() {
			});
			assertEquals(form.getName(), "fuga");
		}
	}

	@Test
	public void testSjis() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							String json = "田中";
							byte[] jsonBytes = json.getBytes(Charset
									.forName("Shift_JIS"));
							res.setContentType("text/plain; charset=Shift_JIS");
							res.getOutputStream().write(jsonBytes);
						}))) {
			MechResponse res = mech.get("/textsjis").execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "田中");
		}
	}

	@Test
	public void testReadJsonUTF8() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							String json = "{\"name\":\"田中\"}";
							byte[] jsonBytes = json.getBytes(Charset
									.forName("UTF-8"));
							res.getOutputStream().write(jsonBytes);
						}))) {
			MechResponse res = mech.get("/readJsonUTF8").execute();
			assertEquals(res.getStatus(), 200);
			Form form = res.readJSON(new TypeReference<Form>() {
			});
			assertEquals(form.getName(), "田中");
		}
	}

	@Test
	public void testReadJsonUTF8Number2() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							String json = "{\"data\":\"田中\"}";
							byte[] jsonBytes = json.getBytes(Charset
									.forName("UTF-8"));
							res.getOutputStream().write(jsonBytes);
						}))) {
			MechResponse res = mech.get("/readJsonUTF8").execute();
			assertEquals(res.getStatus(), 200);
			ApiResponse<String> dat = res
					.readJSON(new TypeReference<ApiResponse<String>>() {
					});
			assertEquals(dat.getData(), "田中");
		}
	}

	public static class ApiResponse<T> {
		T data;

		public T getData() {
			return this.data;
		}

		public void setData(T data) {
			this.data = data;
		}
	}

	@Test
	public void testQuery() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							res.getWriter().write(
									"++x++" + req.getParameter("x"));
						}))) {
			MechResponse res = mech.get("/query?x=y").execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "++x++y");
		}
	}

	@Test
	public void testPostForm() throws UnsupportedEncodingException,
			IOException, Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							req.setCharacterEncoding("UTF-8");
							String name = req.getParameter("name");
							res.setCharacterEncoding("UTF-8");
							res.setContentType("text/plain); charset=utf-8");
							res.getWriter().write(name);
						}))) {
			MechResponse res = mech.post("/postForm").param("name", "pp太郎")
					.execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "pp太郎");
		}
	}

	@Test
	public void testPostMultipart() throws UnsupportedEncodingException,
			FileUploadException, IOException, Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
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
							res.setContentType("text/plain; charset=utf-8");
							res.getWriter()
									.write(name + "XXX" + file.getName());
						}))) {
			MechResponse res = mech.postMultipart("/postMultipart")
					.param("name", "pp太郎").file("file", new File("pom.xml"))
					.execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "pp太郎XXXpom.xml");
		}
	}

	@Test
	public void testSetUserAgent() throws UnsupportedEncodingException,
			FileUploadException, IOException, Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							req.setCharacterEncoding("UTF-8");
							res.setCharacterEncoding("UTF-8");
							res.getWriter().write(req.getHeader("User-Agent"));
						}))) {
			mech.setUserAgent("My own browser");
			MechResponse res = mech.postMultipart("/postMultipart")
					.param("name", "pp太郎").file("file", new File("pom.xml"))
					.execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "My own browser");
		}
	}

	@Test
	public void testSetHeader() throws UnsupportedEncodingException,
			FileUploadException, IOException, Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							req.setCharacterEncoding("UTF-8");
							res.setCharacterEncoding("UTF-8");
							res.getWriter().write(req.getHeader("X-Foo"));
						}))) {
			mech.setHeader("X-Foo", "Bar");
			MechResponse res = mech.postMultipart("/postMultipart")
					.param("name", "pp太郎").file("file", new File("pom.xml"))
					.execute();
			assertEquals(res.getStatus(), 200);
			assertEquals(res.getContentString(), "Bar");
		}
	}

	@Test
	public void testDisableRedirectHandling()
			throws UnsupportedEncodingException,
			FileUploadException, IOException, Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							req.setCharacterEncoding("UTF-8");
							res.setCharacterEncoding("UTF-8");
							if (req.getPathInfo().equals("/")) {
								res.sendRedirect("/x");
							} else {
								res.getWriter().write("HAHA");
							}
						}))) {
			mech.setHeader("X-Foo", "Bar");
			mech.disableRedirectHandling();
			MechResponse res = mech.get("/").execute();
			assertEquals(res.getStatus(), 302);
		}
	}

	@Test
	public void testDump() throws UnsupportedEncodingException,
			FileUploadException, IOException, Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							req.setCharacterEncoding("UTF-8");
							res.setCharacterEncoding("UTF-8");
							if (req.getPathInfo().equals("/")) {
								res.sendRedirect("/x");
							} else {
								res.getWriter().write("HAHA");
							}
						}))) {
			Path tempDir = Files.createTempDirectory("tempfiles");
			System.setProperty("testmech.dump", tempDir.toString());
			mech.disableRedirectHandling();
			mech.get("/").execute();
			File[] listFiles = tempDir.toFile().listFiles();
			assertEquals(1, listFiles.length); // generated 1 file.
		}
	}
}
