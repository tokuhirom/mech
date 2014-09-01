package me.geso.mech;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Client side HTTP request object.
 * 
 * @author tokuhirom
 *
 */
public class MechRequest {

	private final HttpRequestBase request;
	private final Mech mech;

	public MechRequest(Mech mech, HttpRequestBase request) {
		this.request = request;
		this.mech = mech;
	}

	public String getMethod() {
		return this.request.getMethod();
	}

	public MechRequest setHeader(String name, String value) {
		this.request.setHeader(name, value);
		return this;
	}

	public MechRequest addHeader(String name, String value) {
		this.request.addHeader(name, value);
		return this;
	}

	public MechResponse execute() {
		try {
			CloseableHttpClient httpclient = this.mech.getHttpClientBuilder().build();
			CloseableHttpResponse response = httpclient.execute(request);
			return new MechResponse(this, httpclient, response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
