package me.geso.testmech;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.Matchers.*;

import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;

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

	public String getContentType() {
		Header header = getResponse().getFirstHeader("Content-Type");
		if (header != null) {
			return header.getValue();
		} else {
			return null;
		}
	}

	public String getContentString() {
		return new String(getContent(), Charset.forName("UTF-8"));
	}

	public <T> T readJSON(Class<T> valueType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(getContent(), valueType);
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

	public void assertContentTypeStartsWith(String prefix) {
		Assert.assertThat(this.getContentType(), CoreMatchers.startsWith(prefix));
	}

	public void assertContentEquals(String s) {
		Assert.assertThat(this.getContentString(), CoreMatchers.equalTo(s));
	}

	public void assertContentTypeContains(String s) {
		Assert.assertThat(this.getContentType(), CoreMatchers.containsString(s));
	}

	public void assertContentContains(String substring) {
		Assert.assertThat(this.getContentString(), CoreMatchers.containsString(substring));
	}

	public void assertContentTypeEquals(String string) {
		Assert.assertThat(this.getContentType(), CoreMatchers.equalTo(string));
	}

	public CloseableHttpResponse getResponse() {
		return response;
	}

	public byte[] getContent() {
		return content;
	}

}