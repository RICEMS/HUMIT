/*************************************************************************
 * This is the GUI class to visiualise the detecting of notes.
 * User can adjust the sensitivity of detecting notes
 * To change this template, choose Tools | Templates
 * and open the template in the editor.

 * Category: GUI Classes
 *************************************************************************/
package notesdetectingapplication;

import java.io.File;
import java.util.ArrayList;

import nd.hackrice.backend.Context;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ResultsEditorDialog extends Context{

    NotesDetectingApplicationForm parentForm;
    ChartPanel drawingPanel1;
    JFreeChart chart;
    XYSeriesCollection dataset = new XYSeriesCollection();
    XYSeries amplitudeTimeSeries = new XYSeries("wave 1");
    XYSeries averageAmplitudeTimeSeries = new XYSeries("averaged wave ");
    XYSeries OnsetSeries = new XYSeries("Onsets detected");
    XYSeries differentiatedValuesSeries = new XYSeries("Differentiated Amplitudes");
    XYSeries FrequencyInfoSeries = new XYSeries("Onsets Ditected According To Frequency");
    private static final int KEY_ON = 144;   //as defined in MIDI API for java

    /**
     * Creates new form ResultsEditorDialog 
     * @param parent
     * @param modal 
     */
    public ResultsEditorDialog(NotesDetectingApplicationForm parent, boolean modal) {
        parentForm = parent;

        //create an empty chart
        chart = ChartFactory.createXYLineChart("",
                "Time",
                "Amplitude",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);

    }



    private void windowOpenedEventHandler(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowOpenedEventHandler
        //perform basic analysis
        analyze(parentForm.choosenWaveFilePath);
        //find onsets according to initial slider value
        findOnsets(getSliderVal());
    }//GEN-LAST:event_windowOpenedEventHandler
    private double getSliderVal() {
        double val = 64;
        double percentage = (100 - val) / 100.0;
        return 3.09 * percentage;
    }
    public void doJob()
    {
    	windowOpenedEventHandler(null);
    }
    

    /**
     * average Out OriginalWave and create a chart
     * @param dataFromWaveFile 
     */
    private void averageOutOriginalWave(byte[] dataFromWaveFile) {

        int N = dataFromWaveFile.length;
        double[] d = new double[N / 2];
        int numberOfSegments = (int) (dataFromWaveFile.length / parentForm.SEGMENT_SIZE);
        //convert bytes to short values
        for (int i = 0; i < N / 2; i++) {
            d[i] = ((short) (((dataFromWaveFile[2 * i + 1] & 0xFF) << 8) + (dataFromWaveFile[2 * i] & 0xFF))) / ((double) Short.MAX_VALUE);
        }

        //draw original wave and average 
        for (int segmentCount = 0; segmentCount < numberOfSegments; segmentCount++) {
            double positiveAmplitudeAvaregeperSegment = 0;
            int j = 0;
            int positiveCount = 0;
            for (j = 8192 * (segmentCount); j < 8192 * (segmentCount + 1); j++) {
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

    /**
     * detect candidate minimum points and detect onsets
     * @param incrementThreshold 
     */
    private void redrawOnsets(double incrementThreshold) {
        this.findOnsets(incrementThreshold);
        //there are three entries in the onset series for one onset. take the num of gaps
    }

    /**
     * Draw first order differentiation 
     */
    private void drawDifferentiatedAverageAmplitudes() {
        differentiatedValuesSeries.delete(0, differentiatedValuesSeries.getItemCount() - 1);
        MathLibrary myMath = new MathLibrary();
        differentiatedValuesSeries = myMath.CalculateFOD(averageAmplitudeTimeSeries);
    }

    private void findOnsets(double incrementThreshold) {
        MathLibrary mathLibrary = new MathLibrary();
        ArrayList<Double> onsetDetailsList = new ArrayList<Double>();
        OnsetSeries.delete(0, OnsetSeries.getItemCount() - 1);
        onsetDetailsList = mathLibrary.findOnsets(differentiatedValuesSeries, incrementThreshold);

        //copy values to original list
        parentForm.onsetDetailsArray = new double[onsetDetailsList.size()];
        for (int count = 0; count < onsetDetailsList.size(); count++) {
            double onsetTimeValue = onsetDetailsList.get(count);

            //update onset series
            parentForm.onsetDetailsArray[count] = onsetTimeValue;
            double maxY = averageAmplitudeTimeSeries.getMaxY();
            OnsetSeries.add(onsetTimeValue, 0);
            OnsetSeries.add(onsetTimeValue, maxY);
            OnsetSeries.add(onsetTimeValue, 0);
        }
    }

    /*
     * get Differentiate dValues Series
     */
    public XYSeries getDifferentiatedValuesSeries() {
        return differentiatedValuesSeries;
    }

    /**
     * get AverageAmplitude Time Series
     * @return 
     */
    public XYSeries getAverageAmplitudeTimeSeries() {
        return averageAmplitudeTimeSeries;
    }

    /**
     * get Onset Series
     * @return 
     */
    public XYSeries getOnsetSeries() {
        return OnsetSeries;
    }

    /**
     * tell the dialog to calculate display wave shapes
     */
    public void calculateAndDrawWaves() {
        averageOutOriginalWave(parentForm.dataFromWaveFile);
        drawDifferentiatedAverageAmplitudes();
    }

    /**
     * analyze data
     * @param choosenWaveFilePath 
     */
    private void analyze(String choosenWaveFilePath) {
        try {
            //this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SoundIO soundEngine = new SoundIO();
            //READ ORIGINAL WAVE FILE
            parentForm.dataFromWaveFile = soundEngine.readByte(choosenWaveFilePath);

            parentForm.TimeIntervalBetTwoBytes = soundEngine.getTimeBetweenTwoBytesInSeconds();

            //REMOVE VOCALS AND SAVE
            if (parentForm.RemoveVoice == true) {
                parentForm.voiceRemovedData = soundEngine.removeVoice(parentForm.dataFromWaveFile);
                String WavFileWithVocalRemoved = new File(choosenWaveFilePath).getParent() + "\\_vocalRemoved.wav";
                soundEngine.save(WavFileWithVocalRemoved, parentForm.voiceRemovedData);
                parentForm.dataFromWaveFile = soundEngine.readByte(WavFileWithVocalRemoved);

            }

            //PRINT META INFORMATION ON SOUND FILE
            parentForm.PrintMetaInfomation(soundEngine);

            parentForm.ClipLength = soundEngine.getSampleSoundLength();


            //GET APPROXIMATED FREQUENCIES FROM WAV FILE
            parentForm.approximatedTimeAndFrequency = soundEngine.getFrequencies(parentForm.dataFromWaveFile, parentForm.PreferredWindowing);


            //PRINT FREQUENCY DETAIL
            String outputText = "";
            for (int i = 0; i < parentForm.approximatedTimeAndFrequency.length; i++) {
                outputText += "#APPROXIMATED FREQUENCY AT SEGMENT " + i + " AT TIME  "
                        + parentForm.approximatedTimeAndFrequency[i][0] + " IS " + parentForm.approximatedTimeAndFrequency[i][1] + "\n";

                parentForm.setSegmentDataText(outputText);
            }

            calculateAndDrawWaves();
        } finally {
        }
    }
}
