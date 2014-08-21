# testmech

Testing library for web applications. You can test web application based on servlet API very easy.

## SYNOPSIS

	@Test
	public void testRoot() {
		TestMechServlet mech = new TestMechServlet(Servlet.class);
		TestMechResponse res = mech.get("/").execute();
		res.assertSuccess();
		res.assertContentContains("heheh");
	}

	@Test
	public void testHoge() {
		TestMechServlet mech = new TestMechServlet(Servlet.class);
		TestMechResponse res = mech.get("/hogehoge").execute();
		res.assertSuccess();
		res.assertStatusEquals(200);
		res.assertContentTypeContains("iyan");
	}

	@Test
	public void testJson() {
		Form form = new Form("hoge");
		TestMechServlet mech = new TestMechServlet(Servlet.class);
		TestMechResponse res = mech.postJSON("/json", form).execute();
		res.assertSuccess();
		res.assertContentEquals("+++{\"name\":\"hoge\"}+++");
	}

## Install with maven

See http://tokuhirom.github.io/maven/

## DEPENDENCIES

  * jetty
  * Java 1.7+
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

