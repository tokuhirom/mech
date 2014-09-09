package me.geso.mech.synopsis;

import me.geso.mech.Mech;
import me.geso.mech.MechResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by tokuhirom on 9/10/14.
 */
public class SynopsisTest {
    @Test
    public void testGoogle() throws Exception {
        try (Mech mech = new Mech("http://google.com/")) {
            try (MechResponse res = mech.get("/").execute()) {
                assertEquals(200, res.getStatusCode());
            }
        }
    }
}
