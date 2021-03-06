package me.geso.mech;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BufferedHttpEntity;
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
			if (this.mech.hasRequestListener()) {
				// buffer the entity for request listener.
				// I want to use entity body in the logger.
				if (request instanceof HttpEntityEnclosingRequestBase) {
					final HttpEntity entity = ((HttpEntityEnclosingRequestBase) request)
							.getEntity();
					((HttpEntityEnclosingRequestBase) request)
							.setEntity(new BufferedHttpEntity(entity));
				}
			}
			final HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(this.mech.getCookieStore());
			final CloseableHttpClient httpclient = this.mech.getHttpClientBuilder()
					.build();
			final CloseableHttpResponse response = httpclient.execute(request,
					context);
			if (this.mech.hasRequestListener()) {
				// buffer the entity for request listener.
				// I want to use entity body in the logger.
				final HttpEntity entity = response
						.getEntity();
				response.setEntity(new BufferedHttpEntity(entity));
			}
			this.mech.callRequestListener(request, response);
			return new MechResponse(this, httpclient, response, this.mech.getJsonValidator());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
