import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.lang.Math;

public class randomNoise implements PlugInFilter {
    public int setup(String arg, ImagePlus imp) {
        // TODO Auto-generated method stub
        if(arg.equals("about")){
            IJ.showMessage("Count for Pure RGB, White, and Black");
            return DONE;
        } else {
            return DOES_ALL + NO_CHANGES;
        }
    }

    public void run(ImageProcessor ip) {
        // TODO Auto-generated method stub
        // Reading the image
        int row = ip.getHeight();
        int column = ip.getWidth();

        ImagePlus img = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor ip2 = img.getProcessor();
        for(int r = 0; r < row; r++) {
            for(int c = 0; c < column; c++) {
                int pixel = ip.getPixel(c, r);
                int randomNumber = (int)(Math.random()*255);
                pixel+=randomNumber;
                if (pixel>255){
                    pixel = 255;
                }
                ip2.putPixel(c, r, pixel);
            }
        }
        img.show();
    }
}