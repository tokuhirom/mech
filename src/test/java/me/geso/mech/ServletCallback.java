package me.geso.mech;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* Created by tokuhirom on 9/10/14.
*/
@FunctionalInterface
public interface ServletCallback {
    public void service(HttpServletRequest sreq, HttpServletResponse sres)
            throws Exception;
}
