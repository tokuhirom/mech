package me.geso.testmech;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Client side HTTP request object.
 * 
 * @author tokuhirom
 *
 */
public class TestMechRequest {

	private HttpRequestBase request;
	private final HttpClientBuilder httpClientBuilder;

	public TestMechRequest(CookieStore cookieStore, HttpRequestBase request) {
		this.request = request;
		this.httpClientBuilder = HttpClientBuilder.create();
		this.httpClientBuilder.setDefaultCookieStore(cookieStore);
	}

	public String getMethod() {
		return this.request.getMethod();
	}

	public TestMechRequest setHeader(String name, String value) {
		this.request.setHeader(name, value);
		return this;
	}

	public TestMechRequest addHeader(String name, String value) {
		this.request.addHeader(name, value);
		return this;
	}

	public TestMechRequest disableRedirectHandling() {
		this.httpClientBuilder.disableRedirectHandling();
		return this;
	}
	
	public TestMechRequest setRequestConfig(RequestConfig requestConfig) {
		this.httpClientBuilder.setDefaultRequestConfig(requestConfig);
		return this;
	}

	public TestMechResponse execute() {
		try (CloseableHttpClient httpclient = this.httpClientBuilder.build()) {
			try (CloseableHttpResponse response = httpclient
					.execute(request)) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				response.getEntity().writeTo(stream);
				byte[] byteArray = stream.toByteArray();
				return new TestMechResponse(response, byteArray);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
