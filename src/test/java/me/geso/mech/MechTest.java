package me.geso.mech;

import com.fasterxml.jackson.core.type.TypeReference;

import me.geso.tinyvalidator.constraints.NotNull;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.servlet.ServletInputStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MechTest {

	public static class Form {
		@NotNull
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
						}
				)
				)) {
			try (MechResponse res = mech.get("/").execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentType().getMimeType(), "text/plain");
				assertEquals(res.getContentType().getCharset().displayName(),
						"UTF-8");
				assertTrue(res.getContentString().contains("heheh"));
			}
		}
	}

	@Test
	public void testHoge() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							res.setContentType("application/x-iyan; charset=Shift_JIS");
							res.getWriter().write("hogehoge");
						}
				)
				)) {
			try (MechResponse res = mech.get("/hogehoge").execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentType().getMimeType(),
						"application/x-iyan");
				assertEquals(res.getContentType().getCharset().displayName(),
						"Shift_JIS");
			}
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
						}
				)
				)) {
			Form form = new Form("hoge");
			try (MechResponse res = mech.postJSON("/json", form).execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "+++{\n"
						+ "  \"name\" : \"hoge\"\n"
						+ "}+++");
			}
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
						}
				)
				)) {
			Form form = new Form("hoge");
			try (MechResponse res = mech.postJSON("/json?foo=bar", form)
					.execute()) {
				// assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(),
						"+++{\n  \"name\" : \"hoge\"\n}+++");
			}
		}
	}

	@Test
	public void testReadJsonWithTypeReference() throws Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							res.getWriter().write("{\"name\":\"fuga\"}");
						}
				)
				)) {
			try (MechResponse res = mech.get("/readJson").execute()) {
				assertEquals(res.getStatusCode(), 200);
				Form form = res.readJSON(new TypeReference<Form>() {
				});
				assertEquals(form.getName(), "fuga");
			}
		}
	}

	@Test
	public void testReadJsonWithValidationTypeReference() throws Exception {
		boolean gotViolationException = false;
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							res.getWriter().write("{\"name\":null}");
						}
				)
				)) {
			mech.setJsonValidator(Optional.of(new TinyValidatorJsonValidator()));
			try (MechResponse res = mech.get("/readJson").execute()) {
				assertEquals(res.getStatusCode(), 200);
				Form form = res.readJSON(new TypeReference<Form>() {
				});
				assertEquals(form.getName(), "fuga");
			} catch (JsonValidatorViolationException e) {
				gotViolationException = true;
			}
		}
		assertTrue(gotViolationException);
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
						}
				)
				)) {
			try (MechResponse res = mech.get("/textsjis").execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "田中");
			}
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
						}
				)
				)) {
			try (MechResponse res = mech.get("/readJsonUTF8").execute()) {
				assertEquals(res.getStatusCode(), 200);
				Form form = res.readJSON(new TypeReference<Form>() {
				});
				assertEquals(form.getName(), "田中");
			}
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
						}
				)
				)) {
			try (MechResponse res = mech.get("/readJsonUTF8").execute()) {
				assertEquals(res.getStatusCode(), 200);
				ApiResponse<String> dat = res
						.readJSON(new TypeReference<ApiResponse<String>>() {
						});
				assertEquals(dat.getData(), "田中");
			}
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
						}
				)
				)) {
			try (MechResponse res = mech.get("/query?x=y").execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "++x++y");
			}
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
						}
				)
				)) {
			try (MechResponse res = mech.post("/postForm")
					.param("name", "pp太郎")
					.execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "pp太郎");
			}
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
						}
				)
				)) {
			try (MechResponse res = mech.postMultipart("/postMultipart")
					.param("name", "pp太郎").file("file", new File("pom.xml"))
					.execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "pp太郎XXXpom.xml");
			}
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
						}
				)
				)) {
			mech.setUserAgent("My own browser");
			try (MechResponse res = mech.postMultipart("/postMultipart")
					.param("name", "pp太郎").file("file", new File("pom.xml"))
					.execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "My own browser");
			}
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
						}
				)
				)) {
			mech.setHeader("X-Foo", "Bar");
			try (MechResponse res = mech.postMultipart("/postMultipart")
					.param("name", "pp太郎").file("file", new File("pom.xml"))
					.execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "Bar");
			}
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
						}
				)
				)) {
			mech.setHeader("X-Foo", "Bar");
			mech.disableRedirectHandling();
			try (MechResponse res = mech.get("/").execute()) {
				assertEquals(res.getStatusCode(), 302);
			}
		}
	}

	@Test
	public void testRequestListener()
			throws UnsupportedEncodingException,
			FileUploadException, IOException, Exception {
		try (MechJettyServlet mech = new MechJettyServlet(
				new CallbackServlet(
						(req, res) -> {
							req.setCharacterEncoding("UTF-8");
							res.setCharacterEncoding("UTF-8");
							res.getWriter().write("HAHA");
						}
				)
				)) {
			mech.addRequestListener((req, res) -> {
				try {
					final PrintStream out = System.out;
					out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> REQUEST");
					out.println(req.getRequestLine().toString());
					for (Header header : req.getAllHeaders()) {
						out.println(header);
					}
					if (req instanceof HttpEntityEnclosingRequest) {
						out.println("");
						byte[] bytes = EntityUtils
								.toByteArray(((HttpEntityEnclosingRequest) req)
										.getEntity());
						out.write(bytes);
					}

					out.println("");
					out.println("");
					out.println("RESPONSE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
					out.println(res.getStatusLine());
					for (Header header : res.getAllHeaders()) {
						out.println(header);
					}
					out.println("");
					HttpEntity entity = res.getEntity();
					byte[] bytes = EntityUtils
							.toByteArray(entity);
					out.write(bytes);
					out.println("");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			mech.setHeader("X-Foo", "Bar");
			try (MechResponse res = mech.post("/x",
					new StringEntity("hogehoge=fugafuga", "UTF-8")).execute()) {
				assertEquals(res.getStatusCode(), 200);
				assertEquals(res.getContentString(), "HAHA");
			}
		}
	}
}
