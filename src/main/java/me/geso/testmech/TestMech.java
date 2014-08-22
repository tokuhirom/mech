package me.geso.testmech;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;

public class TestMech {
	private BasicCookieStore cookieStore = new BasicCookieStore();
	private String baseURL;

	public TestMech() {
	}

	public TestMech(String baseURL) {
		this.baseURL = baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public void addCookie(Cookie cookie) {
		this.cookieStore.addCookie(cookie);
	}

	public TestMechRequest get(String path) {
		try {
			baseURL = baseURL.replaceFirst("/$", "");
			if (path.startsWith("/")) {
				path = "/" + path;
			}
			URI url = new URI(baseURL + path);
			HttpGet get = new HttpGet(url);
			return new TestMechRequest(cookieStore, get);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> TestMechRequest postJSON(String path, T params) {
		if (params == null) {
			throw new RuntimeException("Params should not be null");
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
			byte[] json = mapper.writeValueAsBytes(params);
			URI url = new URIBuilder(baseURL).setPath(path).build();
			HttpPost post = new HttpPost(url);
			post.setHeader("Content-Type",
					"application/json; charset=utf-8");
			post.setEntity(new ByteArrayEntity(json));
			return new TestMechRequest(cookieStore, post);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
