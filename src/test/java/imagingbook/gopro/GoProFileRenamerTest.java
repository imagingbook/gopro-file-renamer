package imagingbook.gopro;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GoProFileRenamerTest {


    @Test
    public void testGetFileRawName() {
        assertEquals("GH010527", GoProFileRenamer.getFileRawName("GH010527.MP4"));
        assertEquals("GH010527", GoProFileRenamer.getFileRawName("GH010527.foo"));
        assertEquals("GH010527", GoProFileRenamer.getFileRawName("GH010527"));
        assertEquals("foo.bar", GoProFileRenamer.getFileRawName("foo.bar.txt"));
    }

    @Test
    public void testIsGoProFile() {
        GoProFileRenamer renamer = new GoProFileRenamer();
        assertTrue(renamer.isGoProFileName(new File("GH010527.MP4")));
        assertTrue(renamer.isGoProFileName(new File("GH010527.foo")));
        assertTrue(renamer.isGoProFileName(new File("GL010527.foo")));
        assertTrue(renamer.isGoProFileName(new File("GX010527.MP4")));

        assertFalse(renamer.isGoProFileName(new File("HH010527.MP4")));
        assertFalse(renamer.isGoProFileName(new File("GHXX0527.MP4")));
        assertFalse(renamer.isGoProFileName(new File("GH01052Y.MP4")));
    }

}