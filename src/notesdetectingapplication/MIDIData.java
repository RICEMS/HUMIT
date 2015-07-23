/*************************************************************************
 * This class is used to hold information about MIDI events
 * Category: Utility Classes
 *************************************************************************/
package notesdetectingapplication;

public class MIDIData {

    //MIDI key to play
    private int MidiKey;
    //Timer tick value to trigger event
    private long TickNumber;
    //Command to open,close notes or change instruments
    private int MidiCommand;
    //time in actual seconds rather than timerTics
    private double AbsoluteTime;
    //Note played as test
    private String MidiNote;
    //ASCII values to be manupulated in sheet generator
    private String ASCIIValue;
    //Frequency apprximated
    private double ApproximatedFrequency;
    //Segment number relvant to MIDI data
    private int SegmentNumber;

    /**
     * get Frequency
     * @return 
     */
    public double getFrequency() {
        return getApproximatedFrequency();
    }

    /**
     * set Frequency
     * @param frequency 
     */
    public void setFrequency(double frequency) {
        setApproximatedFrequency(frequency);
    }

    /**
     * get ACIIValue
     * @return 
     */
    public String getACIIValue() {
        return ASCIIValue;
    }

    /**
     * set ASCIIValue
     * @param inputASCIIValue 
     */
    public void setASCIIValue(String inputASCIIValue) {
        ASCIIValue = inputASCIIValue;
    }

    /**
     * get MidiKey
     * @return 
     */
    public int getMidiKey() {
        return MidiKey;
    }

    /**
     * get Segment Number
     * @return 
     */
    public int getSegmentNumber() {
        return SegmentNumber;
    }

    /**
     * set Midi Key
     * @param midiKey 
     */
    public void setMidiKey(int midiKey) {
        MidiKey = midiKey;
    }

    /**
     * get Tic Number
     * @return 
     */
    public long getTicNumber() {
        return TickNumber;
    }

    /**
     * set Tick Number
     * @param ticks 
     */
    public void setTickNumber(long ticks) {
        TickNumber = ticks;
    }

    /**
     * get Midi Command
     * @return 
     */
    public int getMidiCommand() {
        return MidiCommand;
    }

    /**
     * set Midi Command
     * @param command 
     */
    public void setMidiCommand(int command) {
        MidiCommand = command;
    }

    /**
     * set Absolute Time
     * @param time 
     */
    public void setAbsoluteTime(double time) {
        AbsoluteTime = time;
    }

    /**
     * get Absolute Time
     * @return 
     */
    public double getAbsoluteTime() {
        return AbsoluteTime;
    }

    /**
     * get MIDI Note
     * @return 
     */
    public String getMIDINote() {
        return MidiNote;
    }

    /**
     * set MIDI Note
     * @param pMiDINote 
     */
    public void setMIDINote(String pMiDINote) {
        MidiNote = pMiDINote;
    }

    /**
     * set Segment Number
     * @param segmentNum 
     */
    public void setSegmentNumber(int segmentNum) {
        SegmentNumber = segmentNum;
    }

    /**
     * @return the ApproximatedFrequency
     */
    public double getApproximatedFrequency() {
        return ApproximatedFrequency;
    }

    /**
     * @param ApproximatedFrequency the Approximated Frequency to set
     */
    public void setApproximatedFrequency(double ApproximatedFrequency) {
        this.ApproximatedFrequency = ApproximatedFrequency;
    }
}
