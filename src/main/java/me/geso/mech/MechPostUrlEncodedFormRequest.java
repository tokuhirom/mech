package me.geso.mech;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class MechPostUrlEncodedFormRequest {

	private final Mech testMech;
	private final HttpPost post;
	private final ArrayList<NameValuePair> params;
	private Charset charset;

	public MechPostUrlEncodedFormRequest(Mech testMech, HttpPost post) {
		this.testMech = testMech;
		this.post = post;
		this.params = new ArrayList<NameValuePair>();
		this.charset = Charset.forName("UTF-8");
	}

	public MechPostUrlEncodedFormRequest setCharset(Charset charset) {
		this.charset = charset;
		return this;
	}

	public MechPostUrlEncodedFormRequest param(String name, String value) {
		this.params.add(new BasicNameValuePair(name, value));
		return this;
	}

	public MechResponse execute() {
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
					this.charset);
			post.setEntity(entity);
			MechRequest request = new MechRequest(
					testMech, post);
			return request.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
