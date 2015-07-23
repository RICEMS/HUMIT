
package notesdetectingapplication;

import org.jfree.chart.ChartPanel;


import java.util.ArrayList;
//import javax.swing.JOptionPane;

import nd.hackrice.backend.Context;
import notesdetectingapplication.InstrumentClassification.InstrumentClassifier;
import notesdetectingapplication.InstrumentClassification.NeuralNetwork;
import notesdetectingapplication.InstrumentClassification.WaveFormData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class InstrumentClassificationDialog extends Context {

    notesdetectingapplication.InstrumentClassification.NeuralNetwork neuralNetwork;
    NotesDetectingApplicationForm parentForm;
    ChartPanel drawingPanel1;
    JFreeChart chart;
    XYSeriesCollection dataset = new XYSeriesCollection();
    XYSeries amplitudeTimeSeries = new XYSeries("wave 1");
    XYSeries averageAmplitudeTimeSeries = new XYSeries("averaged wave ");
    XYSeries differentiatedValuesSeries = new XYSeries("Differentiated Amplitudes");
    XYSeries SustainStartSeries = new XYSeries("Sustain starts detected");
    XYSeries SustainReleaseSeries = new XYSeries("Sustain releases detected");
    XYSeries OnsetSeries = new XYSeries("Onsets detected");
    private final int SEGMENT_SIZE =2048;    //custom segment size for classification only, should be power of 2
    private String ndresdir;
    
    int lookUp=3;
    double cutOffVal=10;
    /** Creates new form InstrumentClassificationDialog */
    public InstrumentClassificationDialog(NotesDetectingApplicationForm parent) {
        parentForm = parent;
        ndresdir=parent.getnDResDir();
        movingWindowAverageOriginalWave(parentForm.dataFromWaveFile);
        initStaffs();
    }
    
    private void initStaffs()
    {
    	drawDifferentiatedAverageAmplitudes();
        drawOnsets();
        //initComponents();
        findSustainStarts(1.32);
        findSustainReleases(1.32);
        //create a NeuralNetwork Instance
        neuralNetwork = new NeuralNetwork(ndresdir);
    }

    private void averageOutOriginalWave(byte[] dataFromWaveFile) {
        int N = dataFromWaveFile.length;
        double[] d = new double[N / 2];
        //For classification we segment using a small window
        int numberOfSegments = (int) (dataFromWaveFile.length / SEGMENT_SIZE);
        //convert bytes to short values
        for (int i = 0; i < N / 2; i++) {
            d[i] = ((short) (((dataFromWaveFile[2 * i + 1] & 0xFF) << 8) + (dataFromWaveFile[2 * i] & 0xFF))) / ((double) Short.MAX_VALUE);
        }

        //draw original wave and average 
        for (int segmentCount = 0; segmentCount < numberOfSegments; segmentCount++) {
            double positiveAmplitudeAvaregeperSegment = 0;
            int j = 0;
            int positiveCount =0;
            for (j = (SEGMENT_SIZE/2) * (segmentCount); j < (SEGMENT_SIZE/2)* (segmentCount + 1); j++) {
                //multiplied by two because two bytes represent a value
                if (j % 100 == 0) {
                    amplitudeTimeSeries.add(j * parentForm.TimeIntervalBetTwoBytes * 2, d[j]);
                }
                if (d[j] > 0) {
                    positiveAmplitudeAvaregeperSegment += d[j];
                    positiveCount++;
                }
            }
            positiveAmplitudeAvaregeperSegment = positiveAmplitudeAvaregeperSegment / positiveCount;
            averageAmplitudeTimeSeries.add(j * parentForm.TimeIntervalBetTwoBytes * 2, positiveAmplitudeAvaregeperSegment);

        }
        
        dataset.addSeries(amplitudeTimeSeries);

        chart = ChartFactory.createXYLineChart("",
                "Time",
                "Amplitude",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
    }
    
    //new method applying moving window averaging to data
    private void movingWindowAverageOriginalWave(byte[] dataFromWaveFile) {
        int N = dataFromWaveFile.length;
        double[] d = new double[N / 2];
        XYSeries temporaryAverageTimeSeries = new XYSeries("Temporary Series");
        //For classification we segment using a small window
        int numberOfSegments = (int) (dataFromWaveFile.length / SEGMENT_SIZE);
        //convert bytes to short values
        for (int i = 0; i < N / 2; i++) {
            d[i] = ((short) (((dataFromWaveFile[2 * i + 1] & 0xFF) << 8) + (dataFromWaveFile[2 * i] & 0xFF))) / ((double) Short.MAX_VALUE);
            if (i % 100 == 0) {
                amplitudeTimeSeries.add(i * parentForm.TimeIntervalBetTwoBytes * 2, d[i]);
            }
        }

        //draw original wave and average 
        for (int segmentCount = 0; segmentCount < numberOfSegments; segmentCount++) {
            int j = 0;
            int valueCount =0;
            double amplitudeAvaregeperSegment=0;            
            for (j = (SEGMENT_SIZE/2) * (segmentCount); j < (SEGMENT_SIZE/2)* (segmentCount + 1); j++) {
                amplitudeAvaregeperSegment += (d[j]*d[j]);
                valueCount++;
            }
            amplitudeAvaregeperSegment = amplitudeAvaregeperSegment / valueCount;
            temporaryAverageTimeSeries.add(j * parentForm.TimeIntervalBetTwoBytes * 2, amplitudeAvaregeperSegment);

        }
        //copy first value
        averageAmplitudeTimeSeries.add(temporaryAverageTimeSeries.getDataItem(0).getX(), temporaryAverageTimeSeries.getDataItem(0).getY());
        //moving average intermediate values
        for(int count=1;count<temporaryAverageTimeSeries.getItemCount()-1;count++)
        {
            double previousVal = temporaryAverageTimeSeries.getDataItem(count-1).getYValue();
            double currentVal =  temporaryAverageTimeSeries.getDataItem(count).getYValue();
            double nextVal =  temporaryAverageTimeSeries.getDataItem(count+1).getYValue();
            double currentTimeVal = temporaryAverageTimeSeries.getDataItem(count).getXValue();
            double average = (previousVal+currentVal+nextVal)/3.0;
            averageAmplitudeTimeSeries.add(currentTimeVal,average);
        }
        //copy last value
        int lastCount = temporaryAverageTimeSeries.getItemCount()-1;
        averageAmplitudeTimeSeries.add(temporaryAverageTimeSeries.getDataItem(lastCount).getX(), temporaryAverageTimeSeries.getDataItem(lastCount).getY());
        
        //draw original wave
        dataset.addSeries(amplitudeTimeSeries);
        chart = ChartFactory.createXYLineChart("",
                "Time",
                "Amplitude",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
    }
    
    private void drawDifferentiatedAverageAmplitudes() {
        differentiatedValuesSeries.delete(0, differentiatedValuesSeries.getItemCount() - 1);
        MathLibrary myMath = new MathLibrary();
        differentiatedValuesSeries = myMath.CalculateFOD(averageAmplitudeTimeSeries);

    }
    private void drawOnsets()
    {
        double[] onsets = parentForm.onsetDetailsArray;
        double maxY = averageAmplitudeTimeSeries.getMaxY();
        for(int count=0; count<onsets.length; count++)
        {      
            OnsetSeries.add(onsets[count],0);
            OnsetSeries.add(onsets[count],maxY);
            OnsetSeries.add(onsets[count],0);
        }  
    }


    private void findSustainStarts(double incrementThreshold) {
        SustainStartSeries.delete(0, SustainStartSeries.getItemCount() - 1);

        notesdetectingapplication.InstrumentClassification.InstrumentClassifier instrumentClasifier = new InstrumentClassifier();
        ArrayList<NotesData> SteadyStates  = instrumentClasifier.FindSustainedTimePeriodsNew
                        (averageAmplitudeTimeSeries,OnsetSeries,cutOffVal,lookUp); 
         for(int count =0; count<SteadyStates.size();count++)
        { 
            double sustainingStartTime = SteadyStates.get(count).getSteadyStateStartTime();
            double maxY = averageAmplitudeTimeSeries.getMaxY();
            SustainStartSeries.add(sustainingStartTime,0);
            SustainStartSeries.add(sustainingStartTime,maxY);
            SustainStartSeries.add(sustainingStartTime,0);
        }
    }

    private void findSustainReleases(double incrementThreshold) {
        SustainReleaseSeries.delete(0, SustainReleaseSeries.getItemCount() - 1);

        notesdetectingapplication.InstrumentClassification.InstrumentClassifier instrumentClasifier = new InstrumentClassifier();
        ArrayList<NotesData> SteadyStates  = instrumentClasifier.FindSustainedTimePeriodsNew
                        (averageAmplitudeTimeSeries,OnsetSeries,cutOffVal,lookUp); 
         for(int count =0; count<SteadyStates.size();count++)
        { 
            double sustainingEndTime = SteadyStates.get(count).getSteadyStateEndTime();
            double maxY = averageAmplitudeTimeSeries.getMaxY();
            SustainReleaseSeries.add(sustainingEndTime,0);
            SustainReleaseSeries.add(sustainingEndTime,maxY);
            SustainReleaseSeries.add(sustainingEndTime,0);
        }
    }
    
    public String classifyInstruments()
    {
        notesdetectingapplication.InstrumentClassification.InstrumentClassifier instrumentClasifier = new InstrumentClassifier();

        ArrayList<Double> BrightnessValues= instrumentClasifier.FindPeakNumberInFirstSegment(parentForm.MIDINotesList,parentForm.PreferredWindowing,parentForm.dataFromWaveFile);
        ArrayList<NotesData> SteadyStates  = instrumentClasifier.FindSustainedTimePeriodsNew
                        (averageAmplitudeTimeSeries,OnsetSeries,cutOffVal,lookUp);
        ArrayList risingSpeedList = instrumentClasifier.FindAverageRisingSpeed(averageAmplitudeTimeSeries,SteadyStates);
        ArrayList releasingSpeedList = instrumentClasifier.FindAverageReleasingSpeed(averageAmplitudeTimeSeries, SteadyStates);
	ArrayList<WaveFormData> ratioList=instrumentClasifier.findRatioBetweenAreas(averageAmplitudeTimeSeries, SteadyStates);
        ArrayList sumOfMomentsList =instrumentClasifier.findSumOfMomentsAboutMidPoint(averageAmplitudeTimeSeries,OnsetSeries);
	ArrayList<ArrayList> data_list = new ArrayList();
        for(int count =0; count<SteadyStates.size();count++)
        {
            int non_zero_data_count = 0;
            if ((Double.toString(SteadyStates.get(count).getSteadyStatePercentage())).equals("NaN")){
              SteadyStates.get(count).setSteadyStatePercentage(0);
            }

            if (releasingSpeedList.get(count).toString().equals("NaN")){
              releasingSpeedList.set(count, 0);
            }

            if (risingSpeedList.get(count).toString().equals("NaN")){
              risingSpeedList.set(count, 0);
            }
            if (BrightnessValues.get(count).toString().equals("NaN")){
              BrightnessValues.set(count, 0.00);
            }
            ArrayList temp_data = new ArrayList();
            temp_data.add(SteadyStates.get(count).getSteadyStatePercentage());
            temp_data.add(Double.parseDouble(BrightnessValues.get(count).toString()));
            temp_data.add(ratioList.get(count).getAvgOfFirstPortion());
            temp_data.add(ratioList.get(count).getAvgOfSecondPortion());
            temp_data.add(ratioList.get(count).getAvgOfThirdPortion());
            data_list.add(temp_data);
            non_zero_data_count++;
        }
        double [][] data = new double[data_list.size()][5];
        for (int h=0;h<data_list.size();h++) {
            ArrayList temp_data = data_list.get(h);
            for (int w=0;w<5;w++) {
                data[h][w] = Double.parseDouble(temp_data.get(w).toString());
            }
        }
        String message = neuralNetwork.queryNetwork(data);
        return message;
    }
    
    /**
     * Get MIDI Instrument number by detecting instrument
     * @return 
     */
    public int getMIDIInstrumentByDetecting()
    {
        String messaage = classifyInstruments();
        if(messaage.equalsIgnoreCase("Flute"))
        {
            return 74;
        }
        else if(messaage.equalsIgnoreCase("Violin"))
        {
            return 41;
        }
        else if(messaage.equalsIgnoreCase("Piano"))
        {
            return 1;
        }
        else if(messaage.equalsIgnoreCase("Guitar"))
        {
            return 26;
        }
        else
        {
            //return piano as instrument
            return 1;
        }
    }
}
