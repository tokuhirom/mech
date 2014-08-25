package me.geso.testmech;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;

import java.nio.charset.Charset;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMechResponse {

	private final CloseableHttpResponse response;
	private final byte[] content;

	public TestMechResponse(CloseableHttpResponse response,
			byte[] content) {
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

	public void assertSuccess() {
		int status = getStatus();
		Assert.assertThat(status, both(greaterThanOrEqualTo(200)).and(lessThan(300)));
	}

	public void assertStatusEquals(int statusCode) {
		int actual = getStatus();
		Assert.assertEquals(statusCode, actual);
	}

	public void assertContentTypeMimeTypeEquals(String mimeType) {
		Assert.assertThat(this.getContentType().getMimeType(), CoreMatchers.equalTo(mimeType));
	}

	public void assertContentEquals(String s) {
		Assert.assertThat(this.getContentString(), CoreMatchers.equalTo(s));
	}

	public void assertContentTypeCharsetEquals(String charsetName) {
		Assert.assertThat(this.getContentType().getCharset(), CoreMatchers.equalTo(Charset.forName(charsetName)));
	}

	public void assertContentContains(String substring) {
		Assert.assertThat(this.getContentString(), CoreMatchers.containsString(substring));
	}

	public CloseableHttpResponse getResponse() {
		return response;
	}

	public byte[] getContent() {
		return content;
	}

}