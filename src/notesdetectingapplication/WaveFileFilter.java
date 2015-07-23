/*************************************************************************
 * This class is a custom file filter for WAV files
 * Category: Utility Classes
 *************************************************************************/
package notesdetectingapplication;

import java.io.*;

public class WaveFileFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".wav");
    }

    public String getDescription() {
        return ".wav files";
    }
}
