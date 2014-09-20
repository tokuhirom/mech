package me.geso.mech.synopsis;

import me.geso.mech.MechJettyServlet;
import me.geso.mech.MechResponse;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.getWriter().write("Hello");
    }

}

public class ServletTest {
    @Test
    public void test() throws Exception {
        try (MechJettyServlet mech = new MechJettyServlet(new MyServlet())) {
            try (MechResponse res = mech.get("/").execute()) {
                assertEquals(200, res.getStatusCode());
                assertEquals("Hello", res.getContentString());
            }
        }
    }
}
