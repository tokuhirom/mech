package me.geso.mech;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

public class MechPostMultipartFormRequest {

	private final Mech testMech;
	private final HttpPost post;
	private MultipartEntityBuilder builder;
	private Charset charset;

	public MechPostMultipartFormRequest(Mech testMech, HttpPost post) {
		this.testMech = testMech;
		this.post = post;
		this.charset = Charset.forName("UTF-8");
		this.builder = MultipartEntityBuilder.create().setCharset(charset);
	}

	public MechPostMultipartFormRequest setCharset(Charset charset) {
		this.charset = charset;
		this.builder.setCharset(charset);
		return this;
	}

	public MechPostMultipartFormRequest param(String name, String text) {
		ContentType contentType = ContentType
				.create("text/plain", this.charset);
		this.builder.addTextBody(name, text, contentType);
		return this;
	}

	public MechPostMultipartFormRequest param(String name, byte[] b) {
		this.builder.addBinaryBody(name, b);
		return this;
	}

	public MechPostMultipartFormRequest file(String name, File file) {
		this.builder.addPart(name, new FileBody(file));
		return this;
	}

	public MechResponse execute() {
		try {
			HttpEntity entity = this.builder.build();
			post.setEntity(entity);
			MechRequest request = new MechRequest(
					testMech, post);
			return request.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
