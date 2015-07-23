/*************************************************************************
 * This is the main GUI class handling all primary functions
 * All interactions with user, specifying proper pipeline flow of data is handled here

 * Category: GUI Classes
 *************************************************************************/
package notesdetectingapplication;

import java.util.ArrayList;
import nd.hackrice.backend.Context;

public class NotesDetectingApplicationForm extends Context {

    public String choosenWaveFilePath = "";
    private String outputText = "";
    //array containing original sound data
    public byte[] dataFromWaveFile;
    //array containing voice removed data
    public double[] voiceRemovedData;
    public final int SEGMENT_SIZE = 16384;
    public double TimeIntervalBetTwoBytes;
    public double ClipLength = 0;
    public ArrayList<MIDIData> MIDINotesList = new ArrayList<MIDIData>();
    double[][] approximatedTimeAndFrequency;
    public double[] onsetDetailsArray;
    private ResultsEditorDialog resultEditorDialog;
    private FilePreferncesDialog preferencesDialog;
    public boolean isUsingFrequecyOnsetDitection = true;
    MIDIFileCreator midiFileCreator = new MIDIFileCreator();
    //JFileChooser openFileChooser = new JFileChooser("C:\\temp\\Samples");
    /**
     * get preferences from dialog and set to this variables
     */
    int MiDIChannel = 1;
    int NotesVelocity = 64;
    int TimingResolution = 1;
    boolean RemoveVoice = false;
    //indicate to classify and detect
    int InitialInstrument = 0;
    int PreferredWindowing = 0;
    String nDResDir;
    
    public void setInitialInstrument(int initialInstrument) {
		InitialInstrument = initialInstrument;
	}


	public String getnDResDir() {
		return nDResDir;
	}


	public void setnDResDir(String nDResDir) {
		this.nDResDir = nDResDir;
	}


	/**
     * Creates new form NotesDetectingApplicationForm
     */
    public NotesDetectingApplicationForm() {


        //Create a Preference Dialog now to show it later
        preferencesDialog = new FilePreferncesDialog(this, true);
    }


    public void setFilePath(String fp)
    {
    	this.choosenWaveFilePath=fp;
    }
    private void preferencesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesButtonActionPerformed
        if (this.RemoveVoice == false) {
            //this.saveVoiceRemovedDataButton.setEnabled(false);
        }
    }//GEN-LAST:event_preferencesButtonActionPerformed

    private void playSongButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playSongButtonActionPerformed
        OriginalSongPlayerDialog songPlayerDialog = new OriginalSongPlayerDialog(this, true);
    }//GEN-LAST:event_playSongButtonActionPerformed
    public void playSong(boolean isOri,int volume,int iType)
    {
    	OriginalSongPlayerDialog songPlayerDialog=new OriginalSongPlayerDialog(this, true);
    	songPlayerDialog.setInstrument(iType);
    	songPlayerDialog.play(isOri,volume);
    }
   
    public void analyse(int instr)
    {
    	analyseButtonActionPerformed();
    }
    private void analyseButtonActionPerformed() {//GEN-FIRST:event_analyseButtonActionPerformed
        //Run garbage collector and try to recycle unused objects
        System.gc();
        try {
            //OPEN ANALYSIS WINDOW
            resultEditorDialog = new ResultsEditorDialog(this, true);
            resultEditorDialog.doJob();
            //resultEditorDialog.setVisible(true);

            //SET PREFERENCES
            midiFileCreator.setChannel(MiDIChannel);
            //set instrument defined in options
            midiFileCreator.setInitialInstrument(InitialInstrument);
            midiFileCreator.setNoteVelocity(NotesVelocity);
            midiFileCreator.setTimeResolution(TimingResolution);

            //if only frequency info is using
            if (isUsingFrequecyOnsetDitection) {
                MIDINotesList = midiFileCreator.preProcessInput(approximatedTimeAndFrequency);
            } //if hybrid approach is used        
            else {
                MIDINotesList = midiFileCreator.processInputforOnsets(approximatedTimeAndFrequency, onsetDetailsArray);
            }

            FormatNotation();

            //PRINT NOTES DETAILS
            PrintNotesInformation();

            //set Progress bar value
            //updateProgress(60, "Notes were analyzed according to onsets");

            //if instrument is to detect detect it
            if(InitialInstrument == 0)
            {
                InstrumentClassificationDialog classificationDialog = new InstrumentClassificationDialog(this);
                int detectedInstrument = classificationDialog.getMIDIInstrumentByDetecting();
                midiFileCreator.setInitialInstrument(detectedInstrument);
                //updateProgress(80, "Instrument was detected");
            }
            //CREATE MIDI FILE IN SEQUENCER
            midiFileCreator.createMIDIFile(MIDINotesList);
        } finally {

        }
    }//GEN-LAST:event_analyseButtonActionPerformed


    public void createMIDIFile(String outpath)
    {
    	midiFileCreator.writeMidiFileToDisk(outpath);
    }


    /**
     * Prints the meta info about WAV file to text area
     * @param soundEngine 
     */
    public void PrintMetaInfomation(SoundIO soundEngine) {
        String stringToSet = ""
                + "Sample Rate: " + soundEngine.getSampleRate() + "\n"
                + "Frame Size: " + soundEngine.getFrameSize() + "\n"
                + "Bits per Sample: " + soundEngine.getBitsPerSample() + "\n"
                + "Sample Sound Length: " + soundEngine.getSampleSoundLength() + "\n"
                + "Number of Equisident Points: " + soundEngine.getNumofEquisidentPoints() + "\n"
                + "Time Interval in Seconds: " + soundEngine.getTimeBetweenTwoBytesInSeconds() + "\n"
                + "Time Interval for a Segment: " + soundEngine.getTimeIntervalForSegment() + "\n"
                + "Sound Channels: " + soundEngine.getMonoStereo() + "\n"
                + "Storage Format: " + soundEngine.getEndianFormat();

        //this.metaInfoTextArea.setText(stringToSet);
    }

    /**
     * Print analyzed information on notes on the text area
     */
    private void PrintNotesInformation() {

        int KEY_ON = 144;   //as defined in MIDI API for java
        int KEY_OFF = 128;  //as defined in MIDI API for java
        String textToSet = "";

        for (int i = 0; i
                < MIDINotesList.size(); i++) {
            MIDIData MidiData = MIDINotesList.get(i);

            if (MidiData.getMidiCommand() == KEY_ON) {
                textToSet = textToSet + "@" + (i + 1) + " SEGMENT NUM = " + MidiData.getSegmentNumber() + ", NOTE = " + MidiData.getMIDINote()
                        + ", ON_TimerTics = " + MidiData.getTicNumber() + ", absolute time = " + MidiData.getAbsoluteTime() + "\n";

            } else if (MidiData.getMidiCommand() == KEY_OFF) {
                textToSet = textToSet + "\t" + "@" + (i + 1) + " SEGMENT NUM = " + MidiData.getSegmentNumber() + ", NOTE = "
                        + MidiData.getMIDINote() + ", OFF_TimerTics = " + MidiData.getTicNumber() + ", absolute time = " + MidiData.getAbsoluteTime() + "\n";
            }
        }

    }


    /**
     * update Midi Note Sheet and Midi File Created
     */
    public void updateMidiNoteSheetandMidiFileCreated() {
        //PRINT NOTES DETAILS
        PrintNotesInformation();

        //CREATE MIDI FILE IN SEQUENCER
        midiFileCreator.createMIDIFile(MIDINotesList);
    }

    /**
     * Format the notes so that sudden changes are normalized
     */
    private void FormatNotation() {
        boolean changed = false;
        int total = 0;
        int totalVals = 0;
        if (MIDINotesList.size() >= 10) {
            for (int i = 0; totalVals < 4; i++) {
                total += MIDINotesList.get(i * 2).getMidiKey();
                if (!(MIDINotesList.get(i * 2).getMidiKey() == 0)) {
                    totalVals += 1;
                }

            }
            int average = Math.round(total / 4);
            int first = MIDINotesList.get(0).getMidiKey();
            if ((average - first) > 7) {
                while ((average - first) > 7) {
                    first += 12;
                }
                changed = true;
            }

            if ((first - average) > 7) {
                while ((first - average) > 7) {
                    first -= 12;
                }
                changed = true;
            }
            if (changed) {
                NotesEditingLibrary notesEditor = new NotesEditingLibrary();
                MIDINotesList.get(0).setMidiKey(first);
                MIDINotesList.get(0).setMIDINote(notesEditor.getMidiNote(first));
                MIDINotesList.get(0).setASCIIValue(notesEditor.getASCIIValueAt(first));
                MIDINotesList.get(0).setApproximatedFrequency(notesEditor.getFrequencyAt(first));
                MIDINotesList.get(1).setMidiKey(first);
                MIDINotesList.get(1).setMIDINote(notesEditor.getMidiNote(first));
                MIDINotesList.get(1).setASCIIValue(notesEditor.getASCIIValueAt(first));
                MIDINotesList.get(1).setApproximatedFrequency(notesEditor.getFrequencyAt(first));

            }
        }
        for (int i = 0; i < MIDINotesList.size() - 2; i = i + 2) {
            changed = false;
            int firstNote = MIDINotesList.get(i).getMidiKey();
            int nextNote = MIDINotesList.get(i + 2).getMidiKey();


            //int OldValNextNote = nextNote;


            if ((firstNote - nextNote) > 7) {
                while ((firstNote - nextNote) > 7) {
                    nextNote += 12;
                }
                changed = true;
            }

            if ((nextNote - firstNote) > 7) {
                while ((nextNote - firstNote) > 7) {
                    nextNote -= 12;
                }
                changed = true;
            }
            //Have to test with results
            int minDiff = Math.min(Math.abs(firstNote - nextNote), Math.min(Math.abs(firstNote - (nextNote + 12)), Math.abs(firstNote - (nextNote - 12))));
            if (minDiff == Math.abs(firstNote - nextNote)) {
            } else if (minDiff == Math.abs(firstNote - (nextNote + 12))) {
                nextNote = nextNote + 12;
                changed = true;
            } else if (minDiff == Math.abs(firstNote - (nextNote - 12))) {
                nextNote = nextNote - 12;
                changed = true;
            }
            //
//            if (OldValNextNote != nextNote) {
//                changed = true;
//            }
            if (changed) {
                NotesEditingLibrary notesEditor = new NotesEditingLibrary();
                MIDINotesList.get(i + 2).setMidiKey(nextNote);
                MIDINotesList.get(i + 2).setMIDINote(notesEditor.getMidiNote(nextNote));
                MIDINotesList.get(i + 2).setASCIIValue(notesEditor.getASCIIValueAt(nextNote));
                MIDINotesList.get(i + 2).setApproximatedFrequency(notesEditor.getFrequencyAt(nextNote));
                MIDINotesList.get(i + 3).setMidiKey(nextNote);
                MIDINotesList.get(i + 3).setMIDINote(notesEditor.getMidiNote(nextNote));
                MIDINotesList.get(i + 3).setASCIIValue(notesEditor.getASCIIValueAt(nextNote));
                MIDINotesList.get(i + 3).setApproximatedFrequency(notesEditor.getFrequencyAt(nextNote));
            }

        }
    }

    /**
     * Set text to segmentData textArea
     * @param text 
     */
    public void setSegmentDataText(String text) {
        outputText = text;
    }

}
