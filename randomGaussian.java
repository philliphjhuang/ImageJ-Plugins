import ij.*;
import ij.plugin.PlugIn;
import ij.process.*;
import java.util.*;

public class randomGaussian implements PlugIn {

    public void run(String arg) {
        // TODO Auto-generated method stub
        int width = 600;
        int height = 480;
        int mean = 150;
        int sd = 50;    
        ImagePlus img = IJ.createImage(null, width, height, 1, 8);
        ImageProcessor ip = img.getProcessor();
        Random rand = new Random();
        for(int r = 0; r < height; r++){        
            for(int c = 0; c < width; c++){
                int randomNumber = (int)(rand.nextGaussian()*sd + mean);
                ip.putPixel(c, r, randomNumber);
            }        
        }
        img.show();
    }
}