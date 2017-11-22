package nl.imine.listener;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TeleportBuildListenerTest {

    private TeleportBuildListener teleportBuildListener;

    @Before
    public void setUp(){
        this.teleportBuildListener = new TeleportBuildListener(null);
    }

    @Test
    public void testRounding() {
        assertEquals(10, teleportBuildListener.roundNearest(7, 10), 1);
        assertEquals(10, teleportBuildListener.roundNearest(5, 10), 1);
        assertEquals(0, teleportBuildListener.roundNearest(4.99, 10), 1);
        assertEquals(-10, teleportBuildListener.roundNearest(-9, 10), 1);
        assertEquals(7.5, teleportBuildListener.roundNearest(7.4, 0.5), 1);
    }
}
