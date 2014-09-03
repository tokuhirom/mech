package me.geso.mech;

import java.io.PrintStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Print request/response to the log. This is useful when you are debugging.
 * 
 * @author tokuhirom
 *
 */
public class PrintRequestListener implements MechRequestListener {
	private PrintStream out;

	/**
	 * Create instance with System.out. This will write the log to stdout.
	 */
	public PrintRequestListener() {
		this(System.out);
	}

	public PrintRequestListener(PrintStream out) {
		this.out = out;
	}

	@Override
	public void call(HttpRequest req, HttpResponse res) {
		try {
			out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> REQUEST");
			out.println(req.getRequestLine().toString());
			for (Header header : req.getAllHeaders()) {
				out.println(header);
			}
			if (req instanceof HttpEntityEnclosingRequest) {
				out.println("");
				byte[] bytes = EntityUtils
						.toByteArray(((HttpEntityEnclosingRequest) req)
								.getEntity());
				out.write(bytes);
			}

			out.println("");
			out.println("");
			out.println("RESPONSE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			out.println(res.getStatusLine());
			for (Header header : res.getAllHeaders()) {
				out.println(header);
			}
			out.println("");
			HttpEntity entity = res.getEntity();
			byte[] bytes = EntityUtils
					.toByteArray(entity);
			out.write(bytes);
			out.println("");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
