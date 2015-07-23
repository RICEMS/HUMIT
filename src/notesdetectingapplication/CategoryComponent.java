/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package notesdetectingapplication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;



/**
 *
 * @author Acer
 */
public class CategoryComponent extends JComponent{
    
    private String[] text = new String[2];
    private BufferedImage img;
    private Color componentColor = new Color(232, 143, 23);
    
    public CategoryComponent() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                componentColor = new Color(255, 143, 23);
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                componentColor = componentColor.brighter();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                componentColor = new Color(232, 143, 23);
                repaint();
            }
        });
        setMinimumSize(new Dimension(500, 100));
        setPreferredSize(new Dimension(500, 100));

        setBorder(BorderFactory.createLineBorder(componentColor.darker(), 2));


        try {
            img = ImageIO.read(getClass().getResourceAsStream("/images/forumIcon.gif"));
        } catch (IOException e) {
            System.err.println("Could not find forum icon.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(componentColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.drawImage(img, 5, 5, null);

        g.setColor(Color.black);
        Font small = new Font("Arial", Font.PLAIN, 12);
        Font large = new Font("Arial", Font.BOLD, 24);

        g.setFont(large);
        g.drawString(text[0], 80, 29);

        g.setFont(small);
        g.drawString("Forums: " + text[1], 80, 50);
    }
}
