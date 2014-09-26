package me.geso.mech;

import java.io.PrintStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Print request/response to the log. This is useful when you are debugging.
 * {@code mech.addRequestListener(new PrintRequestListener(System.out));}
 * 
 * <br>
 * <br>
 * This handler can't show the headers added by HttpClient.
 * 
 * @author tokuhirom
 *
 */
public class PrintRequestListener implements MechRequestListener {
	private final PrintStream out;
	private boolean jsonPrettyPrintFilterEnabled;

	/**
	 * Create instance with System.out. This will write the log to stdout.
	 */
	public PrintRequestListener() {
		this(System.out);
	}

	public PrintRequestListener(PrintStream out) {
		this.out = out;
	}

	public PrintRequestListener enableJsonPrettyPrintFilter() {
		this.jsonPrettyPrintFilterEnabled = true;
		return this;
	}

	@Override
	public void call(HttpRequest req, HttpResponse res) {
		try {
			out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> REQUEST");
			out.println(req.getRequestLine().toString());
			for (final Header header : req.getAllHeaders()) {
				out.println(header);
			}
			if (req instanceof HttpEntityEnclosingRequest) {
				out.println("");
				final byte[] bytes = EntityUtils
						.toByteArray(((HttpEntityEnclosingRequest) req)
								.getEntity());
				out.write(bytes);
			}

			out.println("");
			out.println("");
			out.println("RESPONSE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			out.println(res.getStatusLine());
			for (final Header header : res.getAllHeaders()) {
				out.println(header);
			}
			out.println("");
			final HttpEntity entity = res.getEntity();
			byte[] bytes = EntityUtils
					.toByteArray(entity);
			if (this.jsonPrettyPrintFilterEnabled) {
				final Header contentType = res.getFirstHeader("Content-Type");
				if (contentType != null
						&& contentType.getValue()
								.startsWith("application/json")) {
					final ObjectMapper mapper = new ObjectMapper();
					mapper.enable(SerializationFeature.INDENT_OUTPUT);
					final JsonNode tree = mapper.readTree(bytes);
					bytes = mapper.writeValueAsBytes(tree);
				}
			}
			out.write(bytes);
			out.println("");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
