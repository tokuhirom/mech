package me.geso.mech;

import org.apache.http.entity.StringEntity;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrintRequestListenerTest {

    @Test
    public void testPrintRequestListener()
            throws Exception {
        try (MechJettyServlet mech = new MechJettyServlet(
                new CallbackServlet(
                        (req, res) -> {
                            req.setCharacterEncoding("UTF-8");
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write("HAHA");
                        }
                )
        )) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            mech.addRequestListener(new PrintRequestListener(ps));
            mech.setHeader("X-Foo", "Bar");
            try (MechResponse res = mech.post("/x",
                    new StringEntity("hogehoge=fugafuga", "UTF-8")).execute()) {
                assertEquals(res.getStatusCode(), 200);
                assertEquals(res.getContentString(), "HAHA");
                String output = baos.toString("UTF8");
                assertTrue(output.contains("HAHA"));
                assertTrue(output.contains("hogehoge=fugafuga"));
            }
        }
    }

    @Test
    public void testPrintRequestListenerPrettyPrint()
            throws Exception {
        try (MechJettyServlet mech = new MechJettyServlet(
                new CallbackServlet(
                        (req, res) -> {
                            req.setCharacterEncoding("UTF-8");
                            res.setCharacterEncoding("UTF-8");
                            res.setContentType("application/json");
                            res.getWriter().write("{\"hoge\":[5,9]}");
                        }
                )
        )) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            mech.addRequestListener(new PrintRequestListener(ps).enableJsonPrettyPrintFilter());
            mech.setHeader("X-Foo", "Bar");
            try (MechResponse res = mech.post("/x",
                    new StringEntity("hogehoge=fugafuga", "UTF-8")).execute()) {
                assertEquals(res.getStatusCode(), 200);
                assertEquals(res.getContentString(), "{\"hoge\":[5,9]}");
                String output = baos.toString("UTF8");
                assertTrue(output.contains("X-Foo"));
                assertTrue(output.contains("hogehoge=fugafuga"));
                assertTrue(output.contains("[ 5, 9 ]"));
                System.out.println(output);
            }
        }
    }
}