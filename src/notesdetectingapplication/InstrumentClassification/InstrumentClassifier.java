package notesdetectingapplication.InstrumentClassification;

import java.util.ArrayList;
import notesdetectingapplication.MIDIData;
import notesdetectingapplication.MathLibrary;
import notesdetectingapplication.NotesData;
import notesdetectingapplication.SoundIO;
import org.jfree.data.xy.XYSeries;

public class InstrumentClassifier {

    /**
     * The method for test the threshhold calculation
     * @param array a two dimentional array of frequncy domain data
     */
    public void printArray(double[][] array) {
        double data[] = normalizeArray(array[1]);
        ArrayList<ArrayList> peaks = getBeyoudThreashHold(data);
        for (int i = 0; i < peaks.get(0).size(); i++) {
            System.out.println(i + " Index : " + array[0][Integer.parseInt(peaks.get(0).get(i).toString())] + " Value " + peaks.get(1).get(i));
        }
    }

    /**
     * The brightness calculation method
     * @param array the two dimentional array of frequency domain data
     * @return array of bighness and peak frequency values
     */
    public double[] getBrightnes(double[][] array) {
        double multipliedTotal = 0;
        double totalApmlitide = 0;
        double brightness;
        double peakFrequency = 0 ;
        double numberOfPeaks = 0;
        // Normalize the input array
        double data[] = normalizeArray(array[1]);
        double results [] = new double[2];
        // Get the values beyond the threashold
        ArrayList<ArrayList> peaks = getBeyoudThreashHold(data);
        for (int i = 0; i < peaks.get(0).size(); i++) {
            // skip peaks beyond 2500 range
            if(2500 < array[0][Integer.parseInt(peaks.get(0).get(i).toString())]) {
                numberOfPeaks = i-1;
                break;
            }
            // Get the frequncy of max energy containing peak
            if(Double.parseDouble(peaks.get(1).get(i).toString()) == 100 ) {
                peakFrequency = array[0][Integer.parseInt(peaks.get(0).get(i).toString())];
            }
            System.out.println("Peak X :" +array[0][Integer.parseInt(peaks.get(0).get(i).toString())] + " Value " + peaks.get(1).get(i).toString());
            // Get the summation of K*Ai values
            multipliedTotal = multipliedTotal + (i) * Double.parseDouble(peaks.get(1).get(i).toString()); //(Double.parseDouble(i));
            // Get the summation of Ai values
            totalApmlitide = totalApmlitide + Double.parseDouble(peaks.get(1).get(i).toString());
        }
        // Get the brightness
        brightness = multipliedTotal / totalApmlitide;
        System.out.println("Peak frequency :" + peakFrequency);
        System.out.println("Brighness :" + brightness);
        System.out.println("Number of peacks :" + numberOfPeaks);
        results[0] = brightness;
        results[1] = peakFrequency;
        return results;
    }

    /**
     * Normalize the spectral data from 0 to 100
     * @param data spectral data between two onsets
     * @return max the normalize array
     */
    public double[] normalizeArray(double[] data) {
        double max = getMax(data);
        double min = getMin(data);
        double[] mydata = new double[data.length];
        double oldRange = max - min;
        double newRange = 100;
        for (int i = 0; i < data.length; i++) {
            mydata[i] = ((data[i] - min) * newRange) / (oldRange);
        }
        return mydata;
    }

    /**
     * Get the maximum value out of a given array
     * @param array an array
     * @return
     */
    private double getMax(double[] array) {
        double max = -100;
        for (int i = 0; i < array.length; i++) {
            if (max < array[i]) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * Get the minimum out of a given array
     * @param array an array of values
     * @return
     */
    private double getMin(double[] array) {
        double min = 100;
        for (int i = 0; i < array.length; i++) {
            if (min > array[i]) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * Get the values beyound a threashold value to defferantialte
     * the noise and the peaks
     * @param array a normalize array between two onsets
     * @return resulting array beyond a threshold
     */
    public ArrayList getBeyoudThreashHold(double[] array) {
        double[] threshold = new double[array.length];
        double[] difference = new double[array.length];
        ArrayList<ArrayList> peaks = new ArrayList<ArrayList>();
        peaks.add(new ArrayList<Integer>());
        peaks.add(new ArrayList<Double>());
        double sum = 0;
        double average;
        // Get the summation of first 100 elements
        for (int i = 0; i < 100; i++) {
            sum += array[i];
        }
        // Getting the average of first 100 elements
        average = (sum / 100) + 25;
        // Calculate the peaks within first 100 elements
        for (int i = 0; i < 100; i++) {
            threshold[i] = average;
            if ((array[i] - threshold[i]) > 0) {
                // get the difference array
                difference[i] = array[i] - threshold[i];
                double max = 0;
                int index = i - 1;
                // calculate the peaks value out of difference array
                while (difference[i] > 0) {
                    if (difference[i] > max) {
                        if(index >=0 ) {
                            difference[index] = 0;
                        }
                        max = difference[i];
                        index = i;
                    } else {
                        difference[i] = 0;
                    }
                    i++;

                    if(i==array.length)
                    {
                        break;
                    }
                    //prepare for next value
                    threshold[i] = average;
                    difference[i] = array[i] - threshold[i];
                }
                difference[i] = 0;
                // assign the peak value and the frequncy to an array list
                peaks.get(0).add(index);
                peaks.get(1).add(array[index]);
            } else {
                difference[i] = 0;
            }
        }
        double temp = 0;
        // Calculate the peaks beyound 100 elements
        for (int i = 100; i < array.length; i++) {
            threshold[i] = (((threshold[i - 1] * 100) + array[i] - array[i - 100]) / 100);
            if ((array[i] - threshold[i]) > 0) {
                // Get the difference array
                difference[i] = array[i] - threshold[i];
                double max = 0;
                int index = i - 1;
                // get the peaks of the difference array
                while (difference[i] > 0) {
                    if (difference[i] > max) {
                        difference[index] = 0;
                        max = difference[i];
                        index = i;
                    } else {
                        difference[i] = 0;
                    }
                    i++;

                    if(i==array.length)
                    {
                        break;
                    }
                    // Updating the moving threshold value
                    threshold[i] = (((threshold[i - 1] * 100) + array[i] - array[i - 100]) / 100);
                    difference[i] = array[i] - threshold[i];
                }

                peaks.get(0).add(index);
                peaks.get(1).add(array[index]);

                if(i<array.length)
                {
                    difference[i] = 0;
                }
            } else {
                difference[i] = 0;
            }
        }
        return peaks;
    }
    
     /**
     * Depreciated method
     * @param averageAmplitudeTimeSeries
     * @param OnsetSeries
     * @param threshold
     * @return 
     */
    /**
     * Method for find sustained time periods
     * @param averageAmplitudeTimeSeries
     * @param OnsetSeries
     * @param threshold
     * @return 
     */public ArrayList FindSustainedTimePeriods(XYSeries averageAmplitudeTimeSeries, XYSeries OnsetSeries, double threshold) {

        MathLibrary myMath = new MathLibrary();
        ArrayList<NotesData> SteadyStates = new ArrayList<NotesData>();
        //scan through Onset Seriesbrightness
        for (int i = 0; i < OnsetSeries.getItemCount() - 3; i = i + 3) {
            NotesData notesData = new NotesData();
            int first = averageAmplitudeTimeSeries.indexOf(OnsetSeries.getX(i));
            int l = i + 3;
            Number lNum = OnsetSeries.getX(l);
            int last = averageAmplitudeTimeSeries.indexOf(lNum);

            //create the XY series for the amplitude data of a single note
            XYSeries amplitudesForNote = new XYSeries("AmpWithiNote");
            for (int j = first; j <= last - 1; j++) {
                amplitudesForNote.add(averageAmplitudeTimeSeries.getX(j), averageAmplitudeTimeSeries.getY(j));
            }

            double average = myMath.calculateMean(amplitudesForNote);
            double standardDeviation = myMath.calculateStandardDeviation(amplitudesForNote);
            boolean started = false;
            for (int k = 0; k < amplitudesForNote.getItemCount() - 1; k++) {

                double currentZVal = myMath.GetZValue(amplitudesForNote.getY(k).doubleValue(), average, standardDeviation);
                //new method to find star time and end time
                //originally was 1.32
                if (!myMath.isBeyondThreshold(currentZVal, threshold)) {
                    if (!started) {
                        notesData.setSteadyStateStartTime(amplitudesForNote.getX(k).doubleValue());
                        started = true;
                    }
                    //last value beyond threshold will actuall be recorded 
                    notesData.setSteadyStateEndTime(amplitudesForNote.getX(k).doubleValue());
                }
            }
            notesData.setNoteStartTime(amplitudesForNote.getX(0).doubleValue());
            notesData.setNoteEndTime(amplitudesForNote.getX(amplitudesForNote.getItemCount() - 1).doubleValue());
            double percentage = (notesData.getSteadyStateEndTime() - notesData.getSteadyStateStartTime()) / (notesData.getNoteEndTime() - notesData.getNoteStartTime()) * 100;
            notesData.setSteadyStatePercentage(percentage);
            SteadyStates.add(notesData);
        }
        return SteadyStates;
    }
    
     /**
      * Method for normalize the amplitude series
      * @param averageAmplitudeTimeSeries
      * @param OnsetSeries
      * @return 
      */public XYSeries normalizeAmplitudeSeries(XYSeries averageAmplitudeTimeSeries, XYSeries OnsetSeries){
    //scan through Onset Series
        XYSeries amplitudesForNote = new XYSeries("AmpWithiNote");
        for (int i = 0; i < OnsetSeries.getItemCount() - 3; i = i + 3) {
            int first = averageAmplitudeTimeSeries.indexOf(OnsetSeries.getX(i));
            int l = i + 3;
            Number lNum = OnsetSeries.getX(l);
            int last = averageAmplitudeTimeSeries.indexOf(lNum);
            //create the XY series for the amplitude data of a single note
            for (int j = first; j <= last - 1; j++) {
                amplitudesForNote.add(averageAmplitudeTimeSeries.getX(j), averageAmplitudeTimeSeries.getY(j));
            }
            //normalize all values between 0-100
            double min = amplitudesForNote.getMinY();
            double max = amplitudesForNote.getMaxY();
            //double timeValForMax = 0;
            for (int count = 0; count < amplitudesForNote.getItemCount(); count++) {
                double originalVal = amplitudesForNote.getDataItem(count).getYValue();
                double transfomedVal = ((originalVal - min) / (max - min)) * 100;
                amplitudesForNote.getDataItem(count).setY(transfomedVal);                
            }
        }
            return amplitudesForNote;
    }

    /**
       * method for find the percentage of sustain time periods
       * @param averageAmplitudeTimeSeries
       * @param OnsetSeries
       * @param pcutOffVal
       * @param plookup
       * @return 
       */public ArrayList FindSustainedTimePeriodsNew(XYSeries averageAmplitudeTimeSeries, XYSeries OnsetSeries, double pcutOffVal, int plookup) {
        int lookUp = plookup;
        double cutOffVal = pcutOffVal;
        MathLibrary myMath = new MathLibrary();
        ArrayList<NotesData> SteadyStates = new ArrayList<NotesData>();        
        //scan through Onset Series
       for (int i = 0; i < OnsetSeries.getItemCount() - 3; i = i + 3) {
            NotesData notesData = new NotesData();
            int first = averageAmplitudeTimeSeries.indexOf(OnsetSeries.getX(i));
            int l = i + 3;
            Number lNum = OnsetSeries.getX(l);
            int last = averageAmplitudeTimeSeries.indexOf(lNum);
            //create the XY series for the amplitude data of a single note
            XYSeries amplitudesForNote = new XYSeries("AmpWithiNote");
            for (int j = first; j <= last - 1; j++) {
                amplitudesForNote.add(averageAmplitudeTimeSeries.getX(j), averageAmplitudeTimeSeries.getY(j));
            }
            //normalize all values between 0-100
            double min = amplitudesForNote.getMinY();
            double max = amplitudesForNote.getMaxY();
            double timeValForMax = 0;
          for (int count = 0; count < amplitudesForNote.getItemCount(); count++) {              
                double originalVal = amplitudesForNote.getDataItem(count).getYValue();
                double transfomedVal = ((originalVal - min) / (max - min)) * 100;
                amplitudesForNote.getDataItem(count).setY(transfomedVal);
                //track time value for maximum Y
                if(originalVal == max)
                {
                    timeValForMax = amplitudesForNote.getDataItem(count).getXValue();
                }
            }
            double onsetStartYVal = amplitudesForNote.getY(0).doubleValue();
            double onsetEndYVal = amplitudesForNote.getY(amplitudesForNote.getItemCount() - 1).doubleValue();
            //current sustain start index has to be known to stop scan backward in finding sustain end
            int currentSustainStartIndex = amplitudesForNote.getItemCount()-1;
            boolean started = false;
            //scan forward through the array of normalized amplitudes for sustain beginnings
            for (int k = 0; k < amplitudesForNote.getItemCount() - lookUp - 1; k++) {
                try {
                    double currentNormalizedVal = amplitudesForNote.getDataItem(k).getYValue();
                    double meanOfOncomingVals = myMath.calculateMean(amplitudesForNote.createCopy(k + 1, k + lookUp));
                    if ((Math.abs(currentNormalizedVal - meanOfOncomingVals) < cutOffVal) && (onsetStartYVal <= currentNormalizedVal)) {
                        if (!started && currentNormalizedVal > 50) {
                            notesData.setSteadyStateStartTime(amplitudesForNote.getX(k).doubleValue());
                            notesData.setSteadyStateEndTime(amplitudesForNote.getX(k).doubleValue());
                            notesData.setSteadyStateStartTimeYValue(amplitudesForNote.getY(k).doubleValue());
                            notesData.setSteadyStateEndTimeYValue(amplitudesForNote.getY(k).doubleValue());
                            currentSustainStartIndex = k;
                            started = true;
                            break;
                        }
                    }
                } catch (CloneNotSupportedException ex) {
                    continue;
                }
            }
            boolean ended = false;
            //scan backwards thruogh the array of normalized amplitudes for sustain ends until detected sustain start
            for (int k = amplitudesForNote.getItemCount() - 1; k > currentSustainStartIndex+lookUp; k--) {
                try {
                    double currentNormalizedVal = amplitudesForNote.getDataItem(k).getYValue();
                    double meanOfOncomingVals = myMath.calculateMean(amplitudesForNote.createCopy(k - 1 - lookUp, k - 1));
                    if ((Math.abs(currentNormalizedVal - meanOfOncomingVals) < cutOffVal) && (onsetEndYVal <= currentNormalizedVal)) {
                        if (!ended && currentNormalizedVal > 50) {
                            notesData.setSteadyStateEndTime(amplitudesForNote.getX(k).doubleValue());
                            notesData.setSteadyStateEndTimeYValue(amplitudesForNote.getY(k).doubleValue());
                            ended = true;
                            break;
                        }
                    }
                } catch (CloneNotSupportedException ex) {
                    continue;
                }
            }
            if(notesData.getSteadyStateStartTime() == 0)
            {
                notesData.setSteadyStateStartTime(timeValForMax);
                notesData.setSteadyStateEndTime(timeValForMax);
                notesData.setSteadyStateStartTimeYValue(100);
                notesData.setSteadyStateEndTimeYValue(100);
            }
            notesData.setNoteStartTime(amplitudesForNote.getX(0).doubleValue());
            notesData.setNoteStartTimeYValue(amplitudesForNote.getY(0).doubleValue());
            notesData.setNoteEndTime(amplitudesForNote.getX(amplitudesForNote.getItemCount() - 1).doubleValue());
            notesData.setNoteEndTimeYValue(amplitudesForNote.getY(amplitudesForNote.getItemCount() - 1).doubleValue());
            double percentage = (notesData.getSteadyStateEndTime() - notesData.getSteadyStateStartTime()) / (notesData.getNoteEndTime() - notesData.getNoteStartTime()) * 100;
            notesData.setSteadyStatePercentage(percentage);
            SteadyStates.add(notesData);
        }
        return SteadyStates;
    }

      /**
        * Method for calculate the rising speed of a note
        * @param averageAmplitudeTimeSeries
        * @param SteadyStates
        * @return 
        */public ArrayList FindAverageRisingSpeed(XYSeries averageAmplitudeTimeSeries, ArrayList<NotesData> SteadyStates) {

        ArrayList risingSpeedList = new ArrayList();
        for (int i = 0; i < SteadyStates.size(); i++) {

            double start = SteadyStates.get(i).getNoteStartTime();
            double first = SteadyStates.get(i).getSteadyStateStartTime();
            double staringY = SteadyStates.get(i).getNoteStartTimeYValue();
            double firstY = SteadyStates.get(i).getSteadyStateStartTimeYValue();
            double averageRisingSpeed = (firstY-staringY) / (first - start);
            risingSpeedList.add(averageRisingSpeed);
            }

        return risingSpeedList;
    }

    /**
         * Method for calculate the releasing speed of a note
         * @param averageAmplitudeTimeSeries
         * @param SteadyStates
         * @return 
         */public ArrayList FindAverageReleasingSpeed(XYSeries averageAmplitudeTimeSeries, ArrayList<NotesData> SteadyStates) {
        ArrayList releasingSpeedList = new ArrayList();
        for (int i = 0; i < SteadyStates.size(); i++) {
            double end = SteadyStates.get(i).getNoteEndTime();
            double last = SteadyStates.get(i).getSteadyStateEndTime();
            double endingY = SteadyStates.get(i).getNoteEndTimeYValue();
            double lastY = SteadyStates.get(i).getSteadyStateEndTimeYValue();
            double averageRisingSpeed = (lastY-endingY) / (last - end);
            releasingSpeedList.add(averageRisingSpeed);
        }
        return releasingSpeedList;
    }
    
    
    /**
          * method for find the number of peaks in the first segment 
          * @param MIDINotesList
          * @param preferredWindowing
          * @param dataFromWaveFile
          * @return 
          */public ArrayList<Double> FindPeakNumberInFirstSegment(ArrayList<MIDIData> MIDINotesList, int preferredWindowing, byte[] dataFromWaveFile) {
        SoundIO soundEngine = new SoundIO();
        ArrayList<Double> brightnessInfoList = new ArrayList<Double>();
        for (int i = 0; i < MIDINotesList.size(); i = i + 2) {
            int segmentNumber = MIDINotesList.get(i).getSegmentNumber();
            XYSeries dataset = soundEngine.getPowerSpectrumBetweenSegments(segmentNumber, segmentNumber + 1, preferredWindowing, dataFromWaveFile);
            double[][] spectralData = new double[2][dataset.getItemCount()];
            for (int j = 0; j < dataset.getItemCount(); j++) {
                spectralData[0][j] = dataset.getX(j).doubleValue();
                spectralData[1][j] = dataset.getY(j).doubleValue();
            }
            brightnessInfoList.add(getBrightnes(spectralData)[0]);
        }
        return brightnessInfoList;
    }
    
    /**
           * method for find the ratio between the areas under the wave shape which is divided into 3 parts
           * @param averageAmplitudeTimeSeries
           * @param SteadyStates
           * @return 
           */public ArrayList findRatioBetweenAreas(XYSeries averageAmplitudeTimeSeries, ArrayList<NotesData> SteadyStates){
       ArrayList<WaveFormData> ratioList = new ArrayList<WaveFormData>();
       for (int i = 0; i < SteadyStates.size(); i++) {
            WaveFormData waveFormdata=new WaveFormData();
            double noteStartTime=SteadyStates.get(i).getNoteStartTime();
            double noteEndTime=SteadyStates.get(i).getNoteEndTime();
            int noteStart=averageAmplitudeTimeSeries.indexOf(noteStartTime);
            int noteEnd=averageAmplitudeTimeSeries.indexOf(noteEndTime);
            int startOfSecondPortion=noteStart+(noteEnd-noteStart)/3;
            int startOfThirdPortion=noteStart+(noteEnd-noteStart)*2/3;
            double sumOfFirst=0;
            double sumOfSecond=0;
            double sumOfThird=0;
       
            for (int x=noteStart;x<startOfSecondPortion;x++){
                sumOfFirst=sumOfFirst+averageAmplitudeTimeSeries.getY(x).doubleValue();        
            }
            for (int y=startOfSecondPortion;y<startOfThirdPortion;y++){
                sumOfSecond=sumOfSecond+averageAmplitudeTimeSeries.getY(y).doubleValue();        
            }
            for (int z=startOfThirdPortion;z<noteEnd;z++){
                sumOfThird=sumOfThird+averageAmplitudeTimeSeries.getY(z).doubleValue();        
            }

            double total=sumOfFirst+sumOfSecond+sumOfThird; 
            double avgOfFirst=(sumOfFirst/total)*100;
            double avgOfSecond=(sumOfSecond/total)*100;
            double avgOfThird=(sumOfThird/total)*100;
            waveFormdata.setAvgOfFirstPortion(avgOfFirst);
            waveFormdata.setAvgOfSecondPortion(avgOfSecond);
            waveFormdata.setAvgOfThirdPortion(avgOfThird);
            ratioList.add(waveFormdata);
            System.out.println(ratioList.toString());
            }   
       return ratioList;
       
   }
   
    
    /**
            * Method for find the sum of moments about the mid point of time line of a note
            * @param averageAmplitudeTimeSeries
            * @param OnsetSeries
            * @return 
            */public ArrayList findSumOfMomentsAboutMidPoint(XYSeries averageAmplitudeTimeSeries, XYSeries OnsetSeries) {
        ArrayList sumOfMomentsList = new ArrayList();

        for (int i = 0; i < OnsetSeries.getItemCount() - 3; i = i + 3) {
            int first = averageAmplitudeTimeSeries.indexOf(OnsetSeries.getX(i));
            int l = i + 3;
            Number lNum = OnsetSeries.getX(l);
            int last = averageAmplitudeTimeSeries.indexOf(lNum);
            //create the XY series for the amplitude data of a single note
            XYSeries amplitudesForNote = new XYSeries("AmpWithiNote");
            for (int j = first; j <= last - 1; j++) {
                amplitudesForNote.add(averageAmplitudeTimeSeries.getX(j), averageAmplitudeTimeSeries.getY(j));
            }
            //normalize all values between 0-100
            double min = amplitudesForNote.getMinY();
            double max = amplitudesForNote.getMaxY();
            for (int count = 0; count < amplitudesForNote.getItemCount(); count++) {
                double originalVal = amplitudesForNote.getDataItem(count).getYValue();
                double transfomedVal = ((originalVal - min) / (max - min)) * 100;
                amplitudesForNote.getDataItem(count).setY(transfomedVal);
            }

            int noteStartIndex = 0;
            int noteEndIndex = amplitudesForNote.getItemCount() - 1;
            int midPoint = (int) ((noteStartIndex + noteEndIndex) / 2);
            double sumOfFirstHalf = 0;
            double sumOfSecondHalf = 0;
            for (int x = noteStartIndex; x < midPoint; x++) {
                double amplitude = amplitudesForNote.getY(x).doubleValue();
                sumOfFirstHalf = sumOfFirstHalf + ((x - midPoint) * amplitude);
            }
            for (int y = midPoint; y < noteEndIndex; y++) {
                double amplitude = averageAmplitudeTimeSeries.getY(y).doubleValue();
                sumOfSecondHalf = sumOfSecondHalf + ((y - midPoint) * amplitude);
            }
            double sumOfMoments = sumOfFirstHalf + sumOfSecondHalf;
            sumOfMomentsList.add(sumOfMoments);
        }
        return sumOfMomentsList;
    }
}
