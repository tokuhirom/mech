package me.geso.mech;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* Created by tokuhirom on 9/10/14.
*/
public class CallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ServletCallback callback;

    public CallbackServlet(ServletCallback callback) {
        this.callback = callback;
    }

    public void service(ServletRequest sreq, ServletResponse sres)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) sreq;
        HttpServletResponse res = (HttpServletResponse) sres;
        try {
            this.callback.service(req, res);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
