package me.geso.mech;

import java.nio.charset.Charset;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MechResponse {

	private final CloseableHttpResponse response;
	private final byte[] content;
	private final MechRequest request;

	public MechResponse(MechRequest request, CloseableHttpResponse response,
			byte[] content) {
		this.request = request;
		this.response = response;
		this.content = content;
	}

	public int getStatus() {
		return getResponse().getStatusLine().getStatusCode();
	}

	public String getFirstHeader(String name) {
		return getResponse().getFirstHeader(name).getValue();
	}

	public ContentType getContentType() {
		ContentType contentType = ContentType.getOrDefault(this.response.getEntity());
		return contentType;
	}

	public String getContentString() {
		ContentType contentType = ContentType.getOrDefault(this.response.getEntity());
		return new String(this.content, contentType.getCharset());
	}

	@JsonIgnore
	public String getContentString(Charset charset) {
		return new String(getContent(), charset);
	}

	public <T> T readJSON(Class<T> valueType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(this.content, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T readJSON(TypeReference<T> valueTypeRef) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(this.content, valueTypeRef);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@JsonIgnore
	public CloseableHttpResponse getResponse() {
		return response;
	}

	public byte[] getContent() {
		return content;
	}

	public MechRequest getRequest() {
		return request;
	}

}