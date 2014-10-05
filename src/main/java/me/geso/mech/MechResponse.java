package me.geso.mech;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MechResponse implements Closeable {

	private final CloseableHttpResponse response;
	private byte[] content;
	private final MechRequest request;
	private final CloseableHttpClient httpClient;
	private final Optional<JsonValidator> jsonValidator;

	public MechResponse(MechRequest request, CloseableHttpClient httpClient,
			CloseableHttpResponse response,
			@NonNull final Optional<JsonValidator> jsonValidator) {
		this.request = request;
		this.httpClient = httpClient;
		this.response = response;
		this.jsonValidator = jsonValidator;
	}

	public int getStatusCode() {
		return getResponse().getStatusLine().getStatusCode();
	}

	public Optional<String> getFirstHeader(String name) {
		final Header firstHeader = getResponse().getFirstHeader(name);
		if (firstHeader != null) {
			return Optional.of(firstHeader.getValue());
		} else {
			return Optional.empty();
		}
	}

	public List<String> getHeaders(String name) {
		return Arrays.stream(this.getResponse().getHeaders(name))
				.map(header -> header.getValue())
				.collect(Collectors.toList());
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

	public <T> T readJSON(Class<T> valueType) throws JsonParseException,
			JsonMappingException, IOException, JsonValidatorViolationException {
		ObjectMapper mapper = new ObjectMapper();
		T data = mapper.readValue(this.getContent(), valueType);
		if (this.jsonValidator.isPresent()) {
			Optional<String> errorMessage = this.jsonValidator.get().validate(
					data);
			if (errorMessage.isPresent()) {
				throw new JsonValidatorViolationException(errorMessage.get());
			}
		}
		return data;
	}

	public <T> T readJSON(TypeReference<T> valueTypeRef)
			throws JsonParseException, JsonMappingException, IOException,
			JsonValidatorViolationException {
		ObjectMapper mapper = new ObjectMapper();
		T data = mapper.readValue(this.getContent(), valueTypeRef);
		if (this.jsonValidator.isPresent()) {
			Optional<String> errorMessage = this.jsonValidator.get().validate(
					data);
			if (errorMessage.isPresent()) {
				throw new JsonValidatorViolationException(errorMessage.get());
			}
		}
		return data;
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
		return Arrays.copyOf(content, content.length);
	}

	public MechRequest getRequest() {
		return request;
	}

	@Override
	public void close() throws IOException {
		if (this.httpClient != null) {
			this.httpClient.close();
		}
		if (this.response != null) {
			this.response.close();
		}
	}

}