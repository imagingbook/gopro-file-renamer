package imagingbook.gopro;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GoProFileRenamerTest {


    @Test
    public void testGetFileRawName() {
        GoProFileRenamer renamer = new GoProFileRenamer();
        assertEquals("GH010527", renamer.getFileRawName("GH010527.MP4"));
        assertEquals("GH010527", renamer.getFileRawName("GH010527.foo"));
        assertEquals("GH010527",renamer.getFileRawName("GH010527"));
        assertEquals("foo.bar", renamer.getFileRawName("foo.bar.txt"));
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

    @Test
    public void testMapGoproFileName() {
        GoProFileRenamer renamer = new GoProFileRenamer();
        assertEquals("044601-GH010446.MP4", renamer.mapGoproFileName("GH010446.MP4"));
        assertEquals("044601-GX010446.MP4", renamer.mapGoproFileName("GX010446.MP4"));
        assertEquals("044601-GL010446.MP4", renamer.mapGoproFileName("GL010446.MP4"));
    }

    @Test
    public void testIsRenamedFileName() {
        GoProFileRenamer renamer = new GoProFileRenamer();
        assertTrue(renamer.isRenamedFileName(new File("052701-GH010527.MP4")));
        assertTrue(renamer.isRenamedFileName(new File("052701-GH010527.foo")));
        assertTrue(renamer.isRenamedFileName(new File("052701-GL010527.foo")));
        assertTrue(renamer.isRenamedFileName(new File("052701-GX010527.MP4")));

        assertFalse(renamer.isRenamedFileName(new File("052701-HH010527.MP4")));
        assertFalse(renamer.isRenamedFileName(new File("0527XX-GHXX0527.MP4")));
        assertFalse(renamer.isRenamedFileName(new File("1052Y01-GH01052Y.MP4")));
    }

    @Test
    public void testUnmapGoproFileName() {
        GoProFileRenamer renamer = new GoProFileRenamer();
        assertEquals("GH010446.MP4", renamer.unmapGoproFileName("044601-GH010446.MP4"));
        assertEquals("GX010446.MP4", renamer.unmapGoproFileName("044601-GX010446.MP4"));
        assertEquals("GL010446.MP4", renamer.unmapGoproFileName("044601-GL010446.MP4"));
    }
}