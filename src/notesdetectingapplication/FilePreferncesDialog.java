/*************************************************************************
 * This class is used to set preferences related to music transcription
 * Category: GUI Classes
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *************************************************************************/
package notesdetectingapplication;



import nd.hackrice.backend.Context;

public class FilePreferncesDialog extends Context {

    NotesDetectingApplicationForm parent;

    /** Creates new form FilePreferncesDialog */
    public FilePreferncesDialog(Context parent, boolean modal) {
        this.parent = (NotesDetectingApplicationForm) parent;
    }
    private int GetSelectedInstrument(int selctedIndex) {
        switch (selctedIndex) {
            case 0:
                //call to instrument classifier and know the instrument
                //indicate to perform it later
                return 0;
            case 1:
                return 41;
            case 2:
                return 7;
            case 3:
                return 26;
            case 4:
                return 1;
            case 5:
                return 74;
            case 6:
                return 79;
            default:
                //return piano as the instrument
                return 1;
        }
    }
    
    /**
     * Set the instrument by classifying data
     */
    private int setInstrument()
    {
        InstrumentClassificationDialog classificationDialog = new InstrumentClassificationDialog(parent);
        return classificationDialog.getMIDIInstrumentByDetecting();
    }
}
