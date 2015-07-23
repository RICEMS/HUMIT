/*************************************************************************
 * This class is used to handle operations related to Notes Editing Library
 * Category: Module Classes
 *************************************************************************/
package notesdetectingapplication;

import java.util.ArrayList;

public class NotesEditingLibrary {

    public ArrayList<MIDIData> MIDINotesList = new ArrayList<MIDIData>();
    public ArrayList<MIDIData> OriginalMIDINotesList = new ArrayList<MIDIData>();
    private final String[] notesArr = {"C -1", "C# -1", "D -1", "D# -1", "E -1", "F -1", "F# -1", "G -1", "G# -1",
        "A -1", "A# -1", "B -1", "C 0", "C# 0", "D 0", "D# 0", "E 0", "F 0", "F# 0", "G 0",
        "G# 0", "A 0", "A# 0", "B 0", "C 1", "C# 1", "D 1", "D# 1", "E 1", "F 1", "F# 1",
        "G 1", "G# 1", "A 1", "A# 1", "B 1", "C 2", "C# 2", "D 2", "D# 2", "E 2", "F 2",
        "F# 2", "G 2", "G# 2", "A 2", "A# 2", "B 2", "C 3", "C# 3", "D 3", "D# 3", "E 3",
        "F 3", "F# 3", "G 3", "G# 3", "A 3", "A# 3", "B 3", "C 4", "C# 4", "D 4", "D# 4",
        "E 4", "F 4", "F# 4", "G 4", "G# 4", "A 4", "A# 4", "B 4", "C 5", "C# 5", "D 5",
        "D# 5", "E 5", "F 5", "F# 5", "G 5", "G# 5", "A 5", "A# 5", "B 5", "C 6", "C# 6",
        "D 6", "D# 6", "E 6", "F 6", "F# 6", "G 6", "G# 6", "A 6", "A# 6", "B 6", "C 7",
        "C# 7", "D 7", "D# 7", "E 7", "F 7", "F# 7", "G 7", "G# 7", "A 7", "A# 7", "B 7", "C 8",
        "C# 8", "D 8", "D# 8", "E 8", "F 8", "F# 8", "G 8", "G# 8", "A 8", "A# 8", "B 8", "C 9",
        "C# 9", "D 9", "D# 9", "E 9", "F 9", "F# 9", "G 9"};
    private final String[] instrumentArr = {"Violin", "Guitar", "Piano", "Flute"};
    //Array containing ASCII Value to pass to the notation generator
    private final String[] ASCIIValueArr = {"C,,,", "^C,,,", "D,,,", "^D,,,", "E,,,", "F,,,", "^F,,,", "G,,,", "^G,,,", "A,,,", "^A,,,", "B,,,",
        "C,,", "^C,,", "D,,", "^D,,", "E,,", "F,,", "^F,,", "G,,", "^G,,", "A,,", "^A,,", "B,,", "C,",
        "^C,", "D,", "^D,", "E,", "F,", "^F,", "G,", "^G,", "A,", "^A,", "B,", "C", "^C",
        "D", "^D", "E", "F", "^F", "G", "^G", "A", "^A", "B", "c", "^c", "d",
        "^d", "e", "f", "^f", "g", "^g", "a", "^a", "b", "c'", "^c'", "d'", "^d'",
        "e'", "f'", "^f'", "g'", "^g'", "a'", "^a'", "b'", "c''", "^c''", "d''", "^d''", "e''",
        "f''", "^f''", "g''", "^g''", "a''", "^a''", "b''", "c'''", "^c'''", "d'''", "^d'''",
        "e'''", "f'''", "^f'''", "g'''", "^g'''", "a'''", "^a'''", "b'''", "c''''",
        "^c''''", "d''''", "^d''''", "e''''", "f''''", "^f''''", "g''''",
        "^g''''", "a''''", "^a''''", "b''''", "c'''''", "^c'''''", "d'''''",
        "^d'''''", "e'''''", "f'''''", "^f'''''", "g'''''", "^g'''''",
        "a'''''", "^a'''''", "b'''''", "c''''''", "^c''''''", "d''''''", "^d''''''",
        "e''''''", "f''''''", "^f''''''", "g''''''"};
    private final double[] noteFrequencies = {
        8.176, 8.662, 9.177, 9.723, 10.301, 10.913, 11.562, 12.25, 12.978, 13.75, 14.568, 15.434, 16.352, 17.324, 18.354, 19.445, 20.601, 21.826, 23.124,
        24.499, 25.956, 27.5, 29.135, 30.867, 32.703, 34.648, 36.708, 38.89, 41.203, 43.653, 46.249, 48.999, 51.913, 55, 58.27, 61.735, 65.406, 69.295,
        73.416, 77.781, 82.406, 87.307, 92.499, 97.998, 103.82, 110, 116.54, 123.47, 130.81, 138.59, 146.83, 155.56, 164.81, 174.61, 184.99, 195.99,
        207.65, 220, 233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 391.99, 415.31, 440, 466.16, 439.88, 523.25, 554.37, 587.33, 622.25,
        659.26, 698.46, 739.99, 783.99, 830.61, 880, 932.32, 987.77, 1046.5, 1108.7, 1174.7, 1244.5, 1318.5, 1396.9, 1480, 1568, 1661.2, 1760, 1864.7, 1975.5,
        2093, 2217.5, 2349.3, 2489, 2637, 2793.8, 2960, 3136, 3322.4, 3520, 3729.3, 3951.1, 4186, 4434.9, 4698.6, 4978, 5274, 5587.7, 5919.9, 6271.9, 6644.9, 7040,
        7458.6, 7902.1, 8372, 8869.8, 9397.3, 9956.1, 10548.1, 11175.3, 11839.8, 12543.9
    };

    public NotesEditingLibrary() {
    }

    /**
     * Create a NotesEditingLibrary instance
     * @param pMNotesEditingLibraryIDINotesList 
     */
    NotesEditingLibrary(ArrayList<MIDIData> pMIDINotesList) {
        this.MIDINotesList = pMIDINotesList;
        for (int i = 0; i < MIDINotesList.size(); i++) {
            OriginalMIDINotesList.add(MIDINotesList.get(i));
        }
    }

    /*
     * Delete note at given index
     */
    public void deleteNote(int index) {
        this.MIDINotesList.remove(index);
    }

    /**
     * Get the string names of notes
     * @return 
     */
    public String[] getNoteNamesArray() {
        return notesArr;
    }

    /**
     * Get a list containing names of instruments
     * @return 
     */
    public String[] getInstrumentArray() {
        return instrumentArr;
    }

    /**
     * Get MIDI note by index from notes array
     * @param index
     * @return 
     */
    public String getMidiNote(int index) {
        return notesArr[index];
    }

    /**
     * Get string to be sent to notation sheet by index
     * @param index
     * @return 
     */
    public String getASCIIValueAt(int index) {
        return ASCIIValueArr[index];
    }

    /**
     * Get frequency by index in frequency list
     * @param index
     * @return 
     */
    public Double getFrequencyAt(int index) {
        return noteFrequencies[index];
    }

    /**
     * Get latest MIDI data list
     * @return 
     */
    public ArrayList<MIDIData> getUpdatedMidiDataArray() {
        return MIDINotesList;
    }

    /**
     * Convert absolute time vales to timer tic value
     * @param absTime
     * @return 
     */
    public long convertAbsoluteTimeToTics(double absTime) {
        long timeValueToReturn = (long) (MIDIFileCreator.getFramesPerSecond() * MIDIFileCreator.getTicsPerFrame() * absTime);
        return timeValueToReturn;
    }

    /**
     * Convert timer tic values to original time values
     * (not recommended to use)
     * @param timerTics
     * @return 
     */
    public double convertTimerTicsToAbsoluteTime(long timerTics) {
        double absTime = (double) (timerTics / (MIDIFileCreator.getFramesPerSecond() * MIDIFileCreator.getTicsPerFrame()));
        return absTime;
    }

    /**
     * Returns integer value for input note as a string
     * @param note
     * @return 
     */
    public int findMIDIKeyForNote(String note) {
        int count = 0;
        for (count = 0; count < notesArr.length; count++) {
            if (note.equals(notesArr[count])) {
                break;
            }
        }
        return count;
    }

    /**
     * shift up notes by a tone semi tone or an octave
     * @param resolution
     * @return 
     */
    public boolean shiftUpNotes(int resolution) {
        boolean returnVal = true;
        for (int count = 0; count < MIDINotesList.size(); count++) {
            MIDIData dataElement = MIDINotesList.get(count);
            if(dataElement.getMidiKey()+ resolution<= 127 )
            {
                dataElement.setMidiKey(dataElement.getMidiKey() + resolution);
                dataElement.setMIDINote(getMidiNote(dataElement.getMidiKey()));
                dataElement.setASCIIValue(getASCIIValueAt(dataElement.getMidiKey()));
                dataElement.setApproximatedFrequency(getFrequencyAt(dataElement.getMidiKey()));
            }
            else
            {
                returnVal =false;
                break;
            }
        }
        return returnVal;
    }

    /**
     * shift down notes by a tone semi tone or an octave
     * @param resolution
     * @return 
     */
    public boolean shiftDownNotes(int resolution) {
        boolean returnVal = true;
        for (int count = 0; count < MIDINotesList.size(); count++) {
            MIDIData dataElement = MIDINotesList.get(count);
            if(dataElement.getMidiKey()- resolution>= 0 )
            {
                dataElement.setMidiKey(dataElement.getMidiKey() - resolution);
                dataElement.setMIDINote(getMidiNote(dataElement.getMidiKey()));
                dataElement.setASCIIValue(getASCIIValueAt(dataElement.getMidiKey()));
                dataElement.setApproximatedFrequency(getFrequencyAt(dataElement.getMidiKey()));
            }
            else
            {
                returnVal =false;
                break;
            }
        }
        return returnVal;
    }
}
