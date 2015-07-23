/*************************************************************************
 * This class is the implementation of Notation Sheet Generator module
 * Renders notation sheet using ABC4J library
 * Category: Modular Classes
 *************************************************************************/
package notesdetectingapplication;

import abc.notation.MusicElement;
import abc.notation.Note;
import abc.notation.Tune;
import abc.notation.Tune.Music;
import abc.parser.TuneParser;
import abc.ui.swing.JScoreComponent;
import abc.ui.swing.JScoreElement;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JScrollPane;

public class CustomNotationSheetPanel extends JScoreComponent {

    private ArrayList<Double> notePositionList = new ArrayList<Double>();
    public int notesPositionListIterator = 0;
    private JScrollPane scrollpane;
    Tune tune;

    public CustomNotationSheetPanel(JScrollPane pscrollpane) {
        this.setBackground(Color.blue);
        this.scrollpane = pscrollpane;
    }

    /**
     * Render notation sheet using note sequence
     * @param notesSequence 
     */
    public void createMidiSheetByNotesSequence(String notesSequence) {
        String tuneAsString = "X:0\nT:Notation Output\nM:4/4\nK:C\n" + notesSequence + "\n";
        tune = new TuneParser().parse(tuneAsString);
        this.setTune(tune);

        Music music = tune.getMusic();
        Iterator itMusic = music.iterator();
        while (itMusic.hasNext()) {
            MusicElement me = (MusicElement) itMusic.next();
            if (me instanceof Note) {
                JScoreElement myNote = this.getRenditionElementFor(me);
                if (myNote != null) {
                    double position = myNote.getBoundingBox().getMinX();
                    notePositionList.add(position);
                }
            }
        }
        this.repaint();
    }

    private void scrollRight(int position) {
        scrollpane.getHorizontalScrollBar().setValue(position);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);    // paints background

        int maxY = (int) this.getBounds().getMaxY();
        int minY = (int) this.getBounds().getMinY();
        if (notePositionList != null && notesPositionListIterator < notePositionList.size()) {
            //notes position list has the values w:r:t that panel only
            int x = notePositionList.get(notesPositionListIterator).intValue();
            g.setColor(Color.RED);
            g.drawLine(x, maxY, x, minY);
            //scroll to see the line
            scrollRight(x - 30);
        }

    }
}
