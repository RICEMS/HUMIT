/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package notesdetectingapplication.InstrumentClassification;

/**
 *
 * @author Najla
 */
public class WaveFormData {
    private double avgOfFirstPortion;
    private double avgOfSecondPortion;
    private double avgOfThirdPortion;

    /**
     * @return the avgOfFirstPortion
     */
    public double getAvgOfFirstPortion() {
        return avgOfFirstPortion;
    }

    /**
     * @param avgOfFirstPortion the avgOfFirstPortion to set
     */
    public void setAvgOfFirstPortion(double avgOfFirstPortion) {
        this.avgOfFirstPortion = avgOfFirstPortion;
    }

    /**
     * @return the avgOfSecondPortion
     */
    public double getAvgOfSecondPortion() {
        return avgOfSecondPortion;
    }

    /**
     * @param avgOfSecondPortion the avgOfSecondPortion to set
     */
    public void setAvgOfSecondPortion(double avgOfSecondPortion) {
        this.avgOfSecondPortion = avgOfSecondPortion;
    }

    /**
     * @return the avgOfThirdPortion
     */
    public double getAvgOfThirdPortion() {
        return avgOfThirdPortion;
    }

    /**
     * @param avgOfThirdPortion the avgOfThirdPortion to set
     */
    public void setAvgOfThirdPortion(double avgOfThirdPortion) {
        this.avgOfThirdPortion = avgOfThirdPortion;
    }
    
    public String toString(){
   return getAvgOfFirstPortion()+ " "+getAvgOfSecondPortion()+" "+getAvgOfThirdPortion()+"\n";
    
  }
}
