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

	private URI makeURI(String pathQuery) {
		try {
			baseURL = baseURL.replaceFirst("/$", "");
			if (pathQuery.startsWith("/")) {
				pathQuery = "/" + pathQuery;
			}
			URI url = new URI(baseURL + pathQuery);
			return url;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public TestMechRequest get(String pathQuery) {
		try {
			URI url = makeURI(pathQuery);
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
			URI url = makeURI(path);
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
