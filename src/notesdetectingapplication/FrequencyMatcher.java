/*************************************************************************
 * This class is used to match frequencies to standard note frequencies
 * Category: Utility Classes
 *************************************************************************/
package notesdetectingapplication;

public class FrequencyMatcher {

    private double[] noteFrequencies = {
        8.176, 8.662, 9.177, 9.723, 10.301, 10.913, 11.562, 12.25, 12.978, 13.75, 14.568, 15.434, 16.352, 17.324, 18.354, 19.445, 20.601, 21.826, 23.124,
        24.499, 25.956, 27.5, 29.135, 30.867, 32.703, 34.648, 36.708, 38.89, 41.203, 43.653, 46.249, 48.999, 51.913, 55, 58.27, 61.735, 65.406, 69.295,
        73.416, 77.781, 82.406, 87.307, 92.499, 97.998, 103.82, 110, 116.54, 123.47, 130.81, 138.59, 146.83, 155.56, 164.81, 174.61, 184.99, 195.99,
        207.65, 220, 233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 391.99, 415.31, 440, 466.16, 493.88, 523.25, 554.37, 587.33, 622.25,
        659.26, 698.46, 739.99, 783.99, 830.61, 880, 932.32, 987.77, 1046.5, 1108.7, 1174.7, 1244.5, 1318.5, 1396.9, 1480, 1568, 1661.2, 1760, 1864.7, 1975.5,
        2093, 2217.5, 2349.3, 2489, 2637, 2793.8, 2960, 3136, 3322.4, 3520, 3729.3, 3951.1, 4186, 4434.9, 4698.6, 4978, 5274, 5587.7, 5919.9, 6271.9, 6644.9, 7040,
        7458.6, 7902.1, 8372, 8869.8, 9397.3, 9956.1, 10548.1, 11175.3, 11839.8, 12543.9
    };

    /**
     * get the most matching frequency in the codebook to the given frequency value 
     * @param originalFreq
     * @return 
     */
    public double matchFrequency(double originalFreq) {
        int count = 0;
        while (count < noteFrequencies.length && originalFreq >= noteFrequencies[count]) {
            count++;
        }
        if (count == 0) {
            return noteFrequencies[count];           //if frequency is too low return lowest frequency
        } else if (count == noteFrequencies.length) {
            return noteFrequencies[count - 1];        //if frequency is too high return highest frequency
        } else {
            double upperFreq = noteFrequencies[count];   //this is the least frequency exceeding the original
            double lowerFreq = noteFrequencies[count - 1]; //this is the most frequency lower than the original
            double approximatedFreq = -1;
            if ((originalFreq - lowerFreq) < (upperFreq - originalFreq)) {
                approximatedFreq = lowerFreq;
            } else {
                approximatedFreq = upperFreq;
            }
            //if OF-LF<UF-OF LF is returned
            //if UF-OF<OF-LF UF is returned
            //if they are equal UF is returned

            return approximatedFreq;
        }

    }
}
