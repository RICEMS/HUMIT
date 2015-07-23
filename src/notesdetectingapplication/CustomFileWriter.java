/*************************************************************************
 * This class consists of methods to write different kinds of information
 * To different files given in different formats.
 * Category: Utility Classes
 *************************************************************************/
package notesdetectingapplication;

import java.io.BufferedWriter;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class CustomFileWriter {

    /**
     * write Classifier Information to a .CSV file
     * @param commaSeperatedString
     * @return 
     */
    public boolean writeClassifierInfomation(String commaSeperatedString) {
        boolean successful = writeInformation(commaSeperatedString, ".csv", "csv Files Only");
        return successful;
    }

    /**
     * Write the given text to a .txt file
     * @param text
     * @return 
     */
    public boolean wtriteToTextFile(String text) {
        boolean successful = writeInformation(text, ".txt", "Text files only");
        return successful;
    }

    /**
     * Write voice removed data (or sound data) to a .WAV file
     * @param voiceRemovedData 
     */
    public void writeVoiceRemovedData(double[] voiceRemovedData) {
        //CREATE A WAV FILE WITH REMOVED VOICE
        SoundIO soundEngine = new SoundIO();

        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                if (file.getName().endsWith(".wav") || file.isDirectory()) {
                    return true;
                }
                return false;
            }

            public String getDescription() {
                return "wav files only.";
            }
        };
        fileChooser.setFileFilter(filter);
        int actionDialog = fileChooser.showSaveDialog(null);
        if (actionDialog == JFileChooser.APPROVE_OPTION) {
            File outputFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".wav");
            if (outputFile != null) {
                if (outputFile.exists()) {
                    actionDialog = JOptionPane.showConfirmDialog(null, "Replace existing file?");
                    if (actionDialog == JOptionPane.NO_OPTION) {
                    }
                    if (actionDialog == JOptionPane.YES_OPTION) {
                        soundEngine.save(fileChooser.getSelectedFile().getAbsolutePath() + ".wav", voiceRemovedData);
                        return;
                    }
                } else {
                    soundEngine.save(fileChooser.getSelectedFile().getAbsolutePath() + ".wav", voiceRemovedData);
                }
            }
        }

    }

    /**
     * Write the image on the panel to a PNG file.
     * @param noteSheetPanel
     * @return 
     */
    public boolean writeImageToDiskFromPanel(CustomNotationSheetPanel noteSheetPanel) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                if (file.getName().endsWith(".png") || file.isDirectory()) {
                    return true;
                }
                return false;
            }

            public String getDescription() {
                return "Image files only.";
            }
        };
        fileChooser.setFileFilter(filter);
        int actionDialog = fileChooser.showSaveDialog(null);
        if (actionDialog == JFileChooser.APPROVE_OPTION) {
            File outputFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".png");
            if (outputFile != null) {
                if (outputFile.exists()) {
                    actionDialog = JOptionPane.showConfirmDialog(null, "Replace existing file?");
                    if (actionDialog == JOptionPane.NO_OPTION) {
                    }
                    if (actionDialog == JOptionPane.YES_OPTION) {
                        try {
                            CustomNotationSheetPanel jscore = (CustomNotationSheetPanel) noteSheetPanel;
                            jscore.writeScoreTo(outputFile);
                        } catch (IOException e) {
                            return false;
                        }
                        return true;
                    }
                } else {
                    try {
                        CustomNotationSheetPanel jscore = (CustomNotationSheetPanel) noteSheetPanel;
                        jscore.writeScoreTo(outputFile);
                    } catch (IOException ex) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Write MIDI Information to a .MID file
     * @param midiFileCreator
     * @return 
     */
    public boolean wtriteMIDIInformation(MIDIFileCreator midiFileCreator) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                if (file.getName().endsWith(".mid") || file.isDirectory()) {
                    return true;
                }
                return false;
            }

            public String getDescription() {
                return "MIDI files only.";
            }
        };
        fileChooser.setFileFilter(filter);
        int actionDialog = fileChooser.showSaveDialog(null);
        if (actionDialog == JFileChooser.APPROVE_OPTION) {
            File outputFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".mid");
            if (outputFile != null) {
                if (outputFile.exists()) {
                    actionDialog = JOptionPane.showConfirmDialog(null, "Replace existing file?");
                    if (actionDialog == JOptionPane.NO_OPTION) {
                    }
                    if (actionDialog == JOptionPane.YES_OPTION) {
                        boolean successful = midiFileCreator.writeMidiFileToDisk(fileChooser.getSelectedFile().getAbsolutePath() + ".mid");
                        if (successful == false) {
                            return false;
                        }
                        return true;
                    }
                } else {
                    boolean successful = midiFileCreator.writeMidiFileToDisk(fileChooser.getSelectedFile().getAbsolutePath() + ".mid");
                    if (successful == false) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean writeInformation(String stringToWrite, final String fileFormat, final String description) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                if (file.getName().endsWith(fileFormat) || file.isDirectory()) {
                    return true;
                }
                return false;
            }

            public String getDescription() {
                return description;
            }
        };
        fileChooser.setFileFilter(filter);
        int actionDialog = fileChooser.showSaveDialog(null);
        if (actionDialog == JFileChooser.APPROVE_OPTION) {
            File outputFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + fileFormat);
            if (outputFile != null) {
                if (outputFile.exists()) {
                    actionDialog = JOptionPane.showConfirmDialog(null, "Replace existing file?");
                    if (actionDialog == JOptionPane.NO_OPTION) {
                    }
                    if (actionDialog == JOptionPane.YES_OPTION) {
                        try {
                            BufferedWriter outFile = new BufferedWriter(new FileWriter(outputFile));
                            outFile.write(stringToWrite);
                            outFile.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return false;
                    }
                } else {
                    BufferedWriter outFile = null;
                    try {
                        outFile = new BufferedWriter(new FileWriter(outputFile));
                        outFile.write(stringToWrite);
                        outFile.close();
                    } catch (IOException ex) {
                        return false;
                    } finally {
                        try {
                            outFile.close();
                        } catch (IOException ex) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
