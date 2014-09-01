package me.geso.mech;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MechResponse implements AutoCloseable {

	private final CloseableHttpResponse response;
	private byte[] content;
	private final MechRequest request;
	private final CloseableHttpClient httpClient;

	public MechResponse(MechRequest request, CloseableHttpClient httpClient, CloseableHttpResponse response) {
		this.request = request;
		this.httpClient = httpClient;
		this.response = response;
	}

	public int getStatusCode() {
		return getResponse().getStatusLine().getStatusCode();
	}

	public String getFirstHeader(String name) {
		return getResponse().getFirstHeader(name).getValue();
	}

	public ContentType getContentType() {
		ContentType contentType = ContentType.getOrDefault(this.response
				.getEntity());
		return contentType;
	}

	public String getContentString() {
		ContentType contentType = ContentType.getOrDefault(this.response
				.getEntity());
		return new String(this.getContent(), contentType.getCharset());
	}

	@JsonIgnore
	public String getContentString(Charset charset) {
		return new String(getContent(), charset);
	}

	public <T> T readJSON(Class<T> valueType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(this.getContent(), valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T readJSON(TypeReference<T> valueTypeRef) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(this.getContent(), valueTypeRef);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@JsonIgnore
	public HttpResponse getResponse() {
		return response;
	}

	public byte[] getContent() {
		if (content == null) {
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				response.getEntity().writeTo(stream);
				content = stream.toByteArray();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return content;
	}

	public MechRequest getRequest() {
		return request;
	}

	@Override
	public void close() throws Exception {
		if (this.httpClient != null) {
			this.httpClient.close();
		}
		if (this.response != null) {
			this.response.close();
		}
	}

}