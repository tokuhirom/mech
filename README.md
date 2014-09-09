# mech

[![Build Status](https://travis-ci.org/tokuhirom/mech.svg?branch=master)](https://travis-ci.org/tokuhirom/mech)

Testing library for web applications. You can test web application based on servlet API very easy.

## SYNOPSIS

Access to the external service.

    @Test
    public void testGoogle() throws Exception {
        try (Mech mech = new Mech("http://google.com/")) {
            try (MechResponse res = mech.get("/").execute()) {
                assertEquals(200, res.getStatusCode());
            }
        }
    }

Testing with MechJettyServlet.

    class MyServlet extends HttpServlet {
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

## Features

 * Really easy Servlet testing
 * POST with application/json
 * POST with multipart/form-data
 * fluent interface.

## Install with maven

See http://tokuhirom.github.io/maven/

## DEPENDENCIES

  * jetty
  * Java 1.8+
  * apache httpclient
  * jackson

## API Stability

I will change API without notice. But I may respect the semver.

## LICENSE

    The MIT License (MIT)
    Copyright © 2014 Tokuhiro Matsuno, http://64p.org/ <tokuhirom@gmail.com>

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the “Software”), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
