package imagingbook.gopro;

import java.util.regex.Pattern;

public interface FileNameFormat {

    public boolean matchFileName(String fName);
    public String mapFileName(String fName);

    // ------------------------------------------------------------------------

    public class OriginalGoproFormat implements FileNameFormat {
        // Admissible raw GoPro file names are GHzzxxxx, GLzzxxxx and GXzzxxxx,
        // where zz and xxxx are all decimal digits.
        // The associated regular expression pattern is:
        static final Pattern pat = Pattern.compile("G[HLX]\\d{6}"); // original GoPro file pattern

        @Override
        public boolean matchFileName(String fName) {    // "GH010527.MP4"
            String rName = stripFileExtension(fName);	// "GH010527"
            return pat.matcher(rName).matches();
        }

        @Override
        public String mapFileName(String fName) {       // "GH010446.MP4"
            String videoNo = fName.substring(4, 8);		// "0446"
            String chapNo = fName.substring(2, 4);		// "01"
            return videoNo + chapNo + "-" + fName;		// "044601-GH010446.MP4"
        }
    }

    // ------------------------------------------------------------------------

    public class RenamedGoproFormat implements FileNameFormat {
        // Analogously, this is the pattern for detecting GoPro files that
        // have been renamed by this program:
        static final Pattern pat = Pattern.compile("\\d{6}-" + OriginalGoproFormat.pat.pattern());

        @Override
        public boolean matchFileName(String fName) {    // "052701-GH010527.MP4"
            String rName = stripFileExtension(fName);	// "052701-GH010527"

            if (!pat.matcher(rName).matches())
                return false;

            String videoNo = rName.substring(0, 4);		// "0527"
            if (!videoNo.equals(rName.substring(11, 15)))
                return false;

            String chapNo = fName.substring(4, 6);		// "01"
            if (!chapNo.equals(rName.substring(9, 11)))
                return false;

            return true;
        }

        @Override
        public String mapFileName(String fName) {	    // "044601-GH010446.MP4"
            return fName.substring(7);		            // "GH010446.MP4"
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Strips the file extension from the given file name, for example,
     * "GH010446.MP4" yields "GH010446".
     * @param fileName the file name
     * @return the raw file name without extension
     */
    static String stripFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {	// fileName has no extension
            return fileName;
        }
        return fileName.substring(0, lastIndex);
    }

}
