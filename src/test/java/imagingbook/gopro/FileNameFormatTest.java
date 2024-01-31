package imagingbook.gopro;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileNameFormatTest {

    @Test
    public void testStripFileExtension() {
        GoProFileRenamer renamer = new GoProFileRenamer();
        assertEquals("GH010527", FileNameFormat.stripFileExtension("GH010527.MP4"));
        assertEquals("GH010527", FileNameFormat.stripFileExtension("GH010527.foo"));
        assertEquals("GH010527", FileNameFormat.stripFileExtension("GH010527"));
        assertEquals("foo.bar", FileNameFormat.stripFileExtension("foo.bar.txt"));
    }

    @Test
    public void testMatchFileName1() {
        FileNameFormat renamer = new FileNameFormat.OriginalGoproFormat();
        assertTrue(renamer.matchFileName("GH010527.MP4"));
        assertTrue(renamer.matchFileName("GH010527.foo"));
        assertTrue(renamer.matchFileName("GL010527.foo"));
        assertTrue(renamer.matchFileName("GX010527.MP4"));

        assertFalse(renamer.matchFileName("HH010527.MP4"));
        assertFalse(renamer.matchFileName("GHXX0527.MP4"));
        assertFalse(renamer.matchFileName("GH01052Y.MP4"));
    }

    @Test
    public void testMapFileName1() {
        FileNameFormat renamer = new FileNameFormat.OriginalGoproFormat();
        assertEquals("044601-GH010446.MP4", renamer.mapFileName("GH010446.MP4"));
        assertEquals("044601-GX010446.MP4", renamer.mapFileName("GX010446.MP4"));
        assertEquals("044601-GL010446.MP4", renamer.mapFileName("GL010446.MP4"));
    }

    @Test
    public void testMatchFileName2() {
        FileNameFormat renamer = new FileNameFormat.RenamedGoproFormat();
        assertTrue(renamer.matchFileName("052701-GH010527.MP4"));
        assertTrue(renamer.matchFileName("052701-GH010527.foo"));
        assertTrue(renamer.matchFileName("052701-GL010527.foo"));
        assertTrue(renamer.matchFileName("052701-GX010527.MP4"));

        assertFalse(renamer.matchFileName("052701-HH010527.MP4"));
        assertFalse(renamer.matchFileName("0527XX-GHXX0527.MP4"));
        assertFalse(renamer.matchFileName("1052Y01-GH01052Y.MP4"));
    }

    @Test
    public void testMapFileName2() {
        FileNameFormat renamer = new FileNameFormat.RenamedGoproFormat();
        assertEquals("GH010446.MP4", renamer.mapFileName("044601-GH010446.MP4"));
        assertEquals("GX010446.MP4", renamer.mapFileName("044601-GX010446.MP4"));
        assertEquals("GL010446.MP4", renamer.mapFileName("044601-GL010446.MP4"));
    }
}