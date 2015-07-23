/*************************************************************************
 * This is the GUI class to Edit the notes.
 * This represents the Notes Editor Module
 * To change this template, choose Tools | Templates
 * and open the template in the editor.

 * Category: GUI Classes/Module classes
 *************************************************************************/
package notesdetectingapplication;


import java.util.ArrayList;


import nd.hackrice.backend.Context;

public class EditNotesDialog extends Context {

    NotesEditingLibrary notesEditingLibrary;
    NotesDetectingApplicationForm mainForm;
    TableCellListener tcl;
    ArrayList<MIDIData> globalTempMidiArrayList = new ArrayList<MIDIData>();
    ArrayList<Double> updatedAbsTimeValues = new ArrayList<Double>();
    ArrayList<Double> toneList = new ArrayList<Double>();
    private static final int KEY_ON = 144;   //as defined in MIDI API for java
    private static final int KEY_OFF = 128;  //as defined in MIDI API for java

    /**
     * Creates new form EditNotesDialog 
     * @param parent
     * @param modal 
     */
    public EditNotesDialog(Context parent, boolean modal) {

        mainForm = (NotesDetectingApplicationForm) parent;
        notesEditingLibrary = new NotesEditingLibrary(mainForm.MIDINotesList);

        toneList = mainForm.midiFileCreator.getMathraaList(mainForm.MIDINotesList);
      
        setInstrument();
    }

    /**
     * Enumeration to keep indexes of the table
     */
    private enum NoteTableColumnIndexes {

        Index,
        Select,
        MidiCommand,
        Note,
        Instrument,
        Tone,
        Timer_Tics,
        Absolute_Time
    }



    /**
     * update temporary tone value list which is kept to track old tone value
     */
    private void updateToneList(ArrayList<MIDIData> MIDINotesList) {
        toneList.clear();
        toneList = mainForm.midiFileCreator.getMathraaList(MIDINotesList);
    }



    /**
     * Tone should be a multiple of o.25
     * @param tone
     * @return 
     */
    private boolean validateTone(double tone) {
        boolean returnVal = true;
        if ((tone / 0.25) % 1 != 0.0) {
           
            returnVal = false;
        }
        return returnVal;
    }
    
    /**
     * Set the instrument by classifying data
     */
    private void setInstrument()
    {
        InstrumentClassificationDialog classificationDialog = new InstrumentClassificationDialog(mainForm);
        String instrument = classificationDialog.classifyInstruments();

    }
    
    private int getShiftResolution(int selectedIndex)
    {
            switch (selectedIndex) {
            case 0:
                //increment by a semi tone
                return 1;
            case 1:
                //increment by a full tone
                return 2;
            case 2:
                //increment by an octave
                return 12;
            default:
                return 1;
        }
    }
    
        
        /**
     * Set the detected instrument to the ComboBox
     */
    private String getDetectedInstrument()
    {
        int detectedInstrument = mainForm.midiFileCreator.getInitialInstrument();
        String selectedIndex;
        switch (detectedInstrument) {
            case 41:
                selectedIndex = "Violin";
                break;
            case 74:
                selectedIndex = "Flute";
                break;
            case 26:
                selectedIndex = "Guitar";
                break;
            case 1:
                selectedIndex = "Piano";
                break;
            default:
                //set piano as the instrument
                selectedIndex = "Piano";
        }
        
        return selectedIndex;
    }
}