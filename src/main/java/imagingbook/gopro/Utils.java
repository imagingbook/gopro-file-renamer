package imagingbook.gopro;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class Utils {

    /**
     * Finds the manifest (from META-INF/MANIFEST.MF) of the JAR file
     * from which {@literal clazz} was loaded.
     *
     * See: http://stackoverflow.com/a/1273432
     * @param clazz A class in the JAR file of interest.
     * @return A {@link Manifest} object or {@literal null} if {@literal clazz}
     * was not loaded from a JAR file.
     */
    static Manifest getJarManifest(Class<?> clazz) {
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) { // Class not from JAR
            return null;
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1)
                + "/META-INF/MANIFEST.MF";
        Manifest manifest = null;
        try {
            manifest = new Manifest(new URL(manifestPath).openStream());
        } catch (IOException ignore) { }
        return manifest;
    }

    // String getVersionInfo() {
    //     Manifest mf = getJarManifest(this.getClass());
    //     if (mf == null) {
    //         return "UNKNOWN";
    //     }
    //     //IJ.log("listing attributes");
    //     Attributes attr = mf.getMainAttributes();
    //     String version = null;
    //     String buildTime = null;
    //     try {
    //         version = attr.getValue("Implementation-Version");
    //         buildTime = attr.getValue("Build-Time");
    //     } catch (IllegalArgumentException e) { }
    //     return version + " (" + buildTime + ")";
    // }

    static String getImplementationVersion() {
        Manifest mf = getJarManifest(GoProFileRenamer.class);
        if (mf == null) {
            return null;
        }
        Attributes attr = mf.getMainAttributes();
        String version = null;
        try {
            version = attr.getValue("Implementation-Version");
        } catch (IllegalArgumentException e) { }
        return version;
    }
}
