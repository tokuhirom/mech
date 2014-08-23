package me.geso.testmech;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

public class TestMechPostMultipartFormRequest {

	private final TestMech testMech;
	private final HttpPost post;
	private MultipartEntityBuilder builder;
	private Charset charset;

	public TestMechPostMultipartFormRequest(TestMech testMech, HttpPost post) {
		this.testMech = testMech;
		this.post = post;
		this.charset = Charset.forName("UTF-8");
		this.builder = MultipartEntityBuilder.create().setCharset(charset);
	}
	
	public TestMechPostMultipartFormRequest setCharset(Charset charset) {
		this.charset = charset;
		this.builder.setCharset(charset);
		return this;
	}

	public TestMechPostMultipartFormRequest param(String name, String text) {
		ContentType contentType = ContentType.create("text/plain", this.charset);
		this.builder.addTextBody(name, text, contentType);
		return this;
	}

	public TestMechPostMultipartFormRequest param(String name, byte[] b) {
		this.builder.addBinaryBody(name, b);
		return this;
	}

	public TestMechPostMultipartFormRequest file(String name, File file) {
		this.builder.addPart(name, new FileBody(file));
		return this;
	}

	public TestMechResponse execute() {
		try {
			HttpEntity entity = this.builder.build();
			post.setEntity(entity);
			TestMechRequest request = new TestMechRequest(
					testMech.getCookieStore(), post);
			return request.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	// TODO: setMaxRedirectCount
	// TODO: assertTitleContains?
}
