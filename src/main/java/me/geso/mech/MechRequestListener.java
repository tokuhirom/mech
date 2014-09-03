package me.geso.mech;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

@FunctionalInterface
public interface MechRequestListener {
	public void call(HttpRequest request, HttpResponse response);
}
