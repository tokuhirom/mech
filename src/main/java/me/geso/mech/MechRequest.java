package me.geso.mech;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Client side HTTP request object.
 * 
 * @author tokuhirom
 *
 */
public class MechRequest {

	private HttpRequestBase request;
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
		String dump = System.getProperty("testmech.dump");

		try (CloseableHttpClient httpclient = this.mech.getHttpClientBuilder().build()) {
			try (CloseableHttpResponse response = httpclient
					.execute(request)) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				response.getEntity().writeTo(stream);
				byte[] byteArray = stream.toByteArray();

				MechResponse mechResponse = new MechResponse(this,
						response, byteArray);
				// undocumented feature!
				if (dump != null) {
					Path path = Paths.get(dump);
					String baseName =
							URLEncoder.encode(request.getURI().toASCIIString(),
									"UTF-8")
									+ "-"
									+ System.currentTimeMillis()
									+ ".json";
					File file = path.resolve(baseName).toFile();
					ObjectMapper mapper = new ObjectMapper();
					mapper.enable(SerializationFeature.INDENT_OUTPUT);
					mapper.writeValue(file, mechResponse);
				}
				return mechResponse;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
