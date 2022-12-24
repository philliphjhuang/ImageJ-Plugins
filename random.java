import ij.*;
import ij.plugin.PlugIn;
import ij.process.*;
import java.lang.Math;

public class random implements PlugIn {

    public void run(String arg) {
        // TODO Auto-generated method stub
        int width = 800;
        int height = 800;
        ImagePlus img = IJ.createImage(null, width, height, 1, 8);
        ImageProcessor ip = img.getProcessor();
        for(int r = 0; r < height; r++){        
            for(int c = 0; c < width; c++){
                int randomNumber = (int)(Math.random()*255);
                ip.putPixel(c, r, randomNumber);
            }        
        }
        img.show();
    }
}
