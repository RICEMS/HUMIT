/*************************************************************************
 * This class is used to send information to the printer to print the notation sheet
 * Category: Utility Classes
 *************************************************************************/
package notesdetectingapplication;

import java.awt.print.PrinterJob;
import java.io.FileNotFoundException;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import java.io.FileInputStream;
import java.io.IOException;

public class MusicSheetPrinter {

    /**
     * print the sheet in PNG format given in the path
     * @param path
     * @throws FileNotFoundException
     * @throws PrintException
     * @throws IOException 
     */
    public void printSheet(String path) throws FileNotFoundException, PrintException, IOException {

        FileInputStream fin = null;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        PrinterJob printjob = PrinterJob.getPrinterJob();
        printjob.setJobName("Miyaesi Notation Sheet");
        printjob.printDialog(pras);
        PrintService service = printjob.getPrintService();
        DocPrintJob job = service.createPrintJob();
        fin = new FileInputStream(path);
        Doc doc = new SimpleDoc(fin, DocFlavor.INPUT_STREAM.GIF, null);
        job.print(doc, pras);
        fin.close();
    }
}
