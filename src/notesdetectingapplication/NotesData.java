/*************************************************************************
 * This class keeps information about notes which is used for the classification 
of instruments

 * Category: Utility classes
 *************************************************************************/
package notesdetectingapplication;

public class NotesData {

    private double steadyStateStartTime;
    private double steadyStateEndTime;
    private double steadyStatePercentage;
    private double noteStartTime;
    private double noteEndTime;
    private double steadyStateStartTimeYValue;
    private double steadyStateEndTimeYValue;
    private double noteStartTimeYValue;
    private double noteEndTimeYValue;

    /**
     * @return the steadyStateStartTime
     */
    public double getSteadyStateStartTime() {
        return steadyStateStartTime;
    }

    /**
     * @param steadyStateStartTime the steadyStateStartTime to set
     */
    public void setSteadyStateStartTime(double steadyStateStartTime) {
        this.steadyStateStartTime = steadyStateStartTime;
    }

    /**
     * @return the steadyStateEndTime
     */
    public double getSteadyStateEndTime() {
        return steadyStateEndTime;
    }

    /**
     * @param steadyStateEndTime the steadyStateEndTime to set
     */
    public void setSteadyStateEndTime(double steadyStateEndTime) {
        this.steadyStateEndTime = steadyStateEndTime;
    }

    /**
     * @return the steadyStatePercentage
     */
    public double getSteadyStatePercentage() {
        return steadyStatePercentage;
    }

    /**
     * @param steadyStatePercentage the steadyStatePercentage to set
     */
    public void setSteadyStatePercentage(double steadyStatePercentage) {
        this.steadyStatePercentage = steadyStatePercentage;
    }

    /**
     * @param setSteadyStateStartTimeYValue the setSteadyStateStartTimeYValue to set
     */
    public void setSteadyStateStartTimeYValue(double value) {
        this.steadyStateStartTimeYValue = value;
    }

    /**
     * @return the SteadyStateStartTimeYValue
     */
    public double getSteadyStateStartTimeYValue() {
        return steadyStateStartTimeYValue;
    }

    /**
     * @param setSteadyStateEndTimeYValue the setSteadyStateEndTimeYValue to set
     */
    public void setSteadyStateEndTimeYValue(double value) {
        this.steadyStateEndTimeYValue = value;
    }

    /**
     * @return the SteadyStateEndTimeYValue
     */
    public double getSteadyStateEndTimeYValue() {
        return steadyStateEndTimeYValue;
    }

    public String toString() {
        return getSteadyStateStartTime() + " " + getSteadyStatePercentage() + " " + getSteadyStateEndTime() + " " + getNoteStartTime() + " " + getNoteEndTime() + "\n";

    }

    /**
     * @return the noteStartTime
     */
    public double getNoteStartTime() {
        return noteStartTime;
    }

    /**
     * @param noteStartTime the noteStartTime to set
     */
    public void setNoteStartTime(double noteStartTime) {
        this.noteStartTime = noteStartTime;
    }

    /**
     * @return the noteEndTime
     */
    public double getNoteEndTime() {
        return noteEndTime;
    }

    /**
     * @param noteEndTime the noteEndTime to set
     */
    public void setNoteEndTime(double noteEndTime) {
        this.noteEndTime = noteEndTime;
    }

    /**
     * @return the NoteEndTimeYValue
     */
    public double getNoteEndTimeYValue() {
        return noteEndTimeYValue;
    }

    /**
     * @return the NoteStartTimeYValue
     */
    public double getNoteStartTimeYValue() {
        return noteStartTimeYValue;
    }

    /**
     * @param setNoteEndTimeYValue the setNoteEndTimeYValue to set
     */
    public void setNoteEndTimeYValue(double noteEndTimeYValue) {
        this.noteEndTimeYValue = noteEndTimeYValue;
    }

    /**
     * @param setNoteStartTimeYValue the setNoteStartTimeYValue to set
     */
    public void setNoteStartTimeYValue(double noteStartTimeYValue) {
        this.noteStartTimeYValue = noteStartTimeYValue;
    }
}
