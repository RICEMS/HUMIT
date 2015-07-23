/*************************************************************************
 * This class is used to apply statistical operations on quantized data 
 * representing amplitude values 

 * Category: Utility classes
 *************************************************************************/
package notesdetectingapplication;

import java.util.ArrayList;
import org.jfree.data.xy.XYSeries;

public class MathLibrary {

    /**
     * Calculates mean value
     * @param input
     * @return 
     */
    public double calculateMean(XYSeries input) {
        double total = 0;
        for (int count = 0; count < input.getItemCount(); count++) {
            total += input.getY(count).doubleValue();
        }
        return total / input.getItemCount();
    }

    /**
     * Calculate the standard deviation
     * @param input
     * @return 
     */
    public double calculateStandardDeviation(XYSeries input) {
        double summation = 0;
        double meanValue = calculateMean(input);
        for (int i = 0; i < input.getItemCount(); i++) {
            double y = input.getY(i).doubleValue();
            summation += Math.pow((y - meanValue), 2);
        }
        double variance = summation / input.getItemCount();
        return Math.pow(variance, 0.5);
    }

    /**
     * standarization of data
     * @param y
     * @param meanValue
     * @param StandardDeviation
     * @return 
     */
    private double getZValue(double y, double meanValue, double StandardDeviation) {
        double output = (y - meanValue) / StandardDeviation;
        return output;
    }

    /**
     *
     * @param y
     * @param meanValue
     * @param StandardDeviation
     * @return 
     */
    public double GetZValue(double y, double meanValue, double StandardDeviation) {
        double output = this.getZValue(y, meanValue, StandardDeviation);
        return output;
    }

    /**
     * determine whether rejected or accepted in confidence interval
     * @param inputZValue
     * @param thresholdZValue
     * @return 
     */
    public boolean isBeyondThreshold(double inputZValue, double thresholdZValue) {
        boolean decision = false;
        double absoluteZVal = Math.abs(inputZValue);
        if (absoluteZVal > thresholdZValue) {
            decision = true;
        }
        return decision;
    }

    /**
     * Detects onsets of the whole wave considering only positive increments 
     * @param differentiatedData
     * @param thresholdZValue
     * @return 
     */
    public ArrayList<Double> findOnsets(XYSeries differentiatedData, double thresholdZValue) {
        ArrayList<Double> onsetsArray = new ArrayList<Double>();
        double meanValue = calculateMean(differentiatedData);
        double standardDeviation = calculateStandardDeviation(differentiatedData);
        
        for (int i = 0; i < differentiatedData.getItemCount(); i++) {
            double currentY = differentiatedData.getY(i).doubleValue();
            double currentX = differentiatedData.getX(i).doubleValue();
            
            if(i == 0 || i == differentiatedData.getItemCount()-1)
            {
                onsetsArray.add(currentX);
                continue;
            }
            
            double currentZVal = getZValue(currentY, meanValue, standardDeviation);
            if (currentY > 0) {
                if (isBeyondThreshold(currentZVal, thresholdZValue)) {
                    onsetsArray.add(currentX);
                }
            }
        }
        return onsetsArray;
    }

    /**
     * Calculates the z-scores of a whole value series 
     * @param valueSet
     * @return 
     */
    public XYSeries CalculateZScores(XYSeries valueSet) {
        XYSeries zScores = new XYSeries("zScores");
        double average = this.calculateMean(valueSet);
        double sd = this.calculateStandardDeviation(valueSet);
        for (int i = 0; i < valueSet.getItemCount() - 1; i++) {
            double zVal = this.getZValue(valueSet.getY(i).doubleValue(), average, sd);
            zScores.add(valueSet.getX(i), zVal);
        }
        return zScores;
    }

    /**
     * Calculates the first order differentiation of valueSet
     * @param valueSet
     * @return 
     */
    public XYSeries CalculateFOD(XYSeries valueSet) {
        XYSeries fod = new XYSeries("fod");
        for (int count = 0; count < valueSet.getItemCount() - 1; count++) {
            double currentY = valueSet.getY(count).doubleValue();
            double upComingY = valueSet.getY(count + 1).doubleValue();
            double currentX = valueSet.getX(count).doubleValue();
            double upComingX = valueSet.getX(count + 1).doubleValue();
            //apply simple differentiation rule digitally
            double increment = (upComingY - currentY) / (upComingX - currentX);
            fod.add(currentX, increment);
        }
        return fod;
    }
}
