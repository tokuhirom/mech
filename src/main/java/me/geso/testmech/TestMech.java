package me.geso.testmech;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestMech {
	private String baseURL;
	private final HeaderGroup defaultHeaders = new HeaderGroup();
	private final HttpClientBuilder httpClientBuilder;
	private CookieStore cookieStore = new BasicCookieStore();

	public TestMech() {
		this.httpClientBuilder = HttpClientBuilder.create();
	}

	public TestMech(String baseURL) {
		this.baseURL = baseURL;
		this.httpClientBuilder = HttpClientBuilder.create();
		this.httpClientBuilder.setDefaultCookieStore(cookieStore);
	}
	
	public HttpClientBuilder getHttpClientBuilder() {
		return this.httpClientBuilder;
	}

	public void setHeader(String name, String value) {
		this.defaultHeaders.updateHeader(new BasicHeader(name, value));
	}

	public void setUserAgent(String userAgent) {
		this.defaultHeaders.updateHeader(new BasicHeader("User-Agent",
				userAgent));
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * <code>
	 * BasicClientCookie cookie = new BasicClientCookie("key", "value");
	 * cookie.setDomain("domain");
	 * cookie.setPath("/");
	 * mech.addCookie(cookie);
	 * </code>
	 * 
	 * @param cookie
	 */
	public void addCookie(Cookie cookie) {
		this.getCookieStore().addCookie(cookie);
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
			this.setDefaultHeaders(get);
			return new TestMechRequest(this, get);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setDefaultHeaders(HttpRequestBase req) {
		HeaderIterator iterator = this.defaultHeaders.iterator();
		while (iterator.hasNext()) {
			Header header = iterator.nextHeader();
			req.addHeader(header);
		}
	}

	public TestMech disableRedirectHandling() {
		this.getHttpClientBuilder().disableRedirectHandling();
		return this;
	}

	public TestMech setRequestConfig(RequestConfig requestConfig) {
		this.getHttpClientBuilder().setDefaultRequestConfig(requestConfig);
		return this;
	}

	public <T> TestMechRequest postJSON(String path, T params) {
		if (params == null) {
			throw new RuntimeException("Params should not be null");
		}

		try {
			ObjectMapper mapper = this.createObjectMapper();

			byte[] json = mapper.writeValueAsBytes(params);
			URI url = makeURI(path);
			HttpPost post = new HttpPost(url);
			this.setDefaultHeaders(post);
			post.setHeader("Content-Type",
					"application/json; charset=utf-8");
			post.setEntity(new ByteArrayEntity(json));
			return new TestMechRequest(this, post);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper;
	}

	public <T> TestMechRequest post(String path, HttpEntity entity) {
		if (entity == null) {
			throw new RuntimeException("Entity should not be null");
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
			URI url = makeURI(path);
			HttpPost post = new HttpPost(url);
			this.setDefaultHeaders(post);
			post.setEntity(entity);
			return new TestMechRequest(this, post);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> TestMechPostUrlEncodedFormRequest post(String path) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
			URI url = makeURI(path);
			HttpPost post = new HttpPost(url);
			this.setDefaultHeaders(post);
			return new TestMechPostUrlEncodedFormRequest(this, post);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> TestMechPostMultipartFormRequest postMultipart(String path) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
			URI url = makeURI(path);
			HttpPost post = new HttpPost(url);
			this.setDefaultHeaders(post);
			return new TestMechPostMultipartFormRequest(this, post);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setProxy(HttpHost proxy) {
		this.httpClientBuilder.setProxy(proxy);
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}
}
