package me.geso.testmech;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class TestMechPostUrlEncodedFormRequest {

	private final TestMech testMech;
	private final HttpPost post;
	private final ArrayList<NameValuePair> params;
	private Charset charset;

	public TestMechPostUrlEncodedFormRequest(TestMech testMech, HttpPost post) {
		this.testMech = testMech;
		this.post = post;
		this.params = new ArrayList<NameValuePair>();
		this.charset = Charset.forName("UTF-8");
	}
	
	public TestMechPostUrlEncodedFormRequest setCharset(Charset charset) {
		this.charset = charset;
		return this;
	}

	public TestMechPostUrlEncodedFormRequest param(String name, String value) {
		this.params.add(new BasicNameValuePair(name, value));
		return this;
	}

	public TestMechResponse execute() {
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, this.charset);
			System.out.println(entity.getContentEncoding());
			post.setEntity(entity);
			TestMechRequest request = new TestMechRequest(
					testMech.getCookieStore(), post);
			return request.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
