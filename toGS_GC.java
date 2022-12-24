import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.lang.Math;

public class toGS_GC implements PlugInFilter {
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
        int K = 256;
        int aMax = K-1;
        double GAMMA = 4;

        ImagePlus img = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor ip2 = img.getProcessor();
        
        for(int r = 0; r < row; r++) {
            for(int c = 0; c < column; c++) {

                //Get RGB
                int pixel = ip.getPixel(c, r);
                int red = (pixel & 0xff0000) >> 16;
                int green = (pixel & 0x00ff00) >> 8;
                int blue = (pixel & 0x0000ff);

                //Grayscale
                int average = (red+blue+green)/3;

                //Gamma correction
                double temp = (double)average/aMax;
                int gammaCorrectionValue = (int)(Math.pow(temp, GAMMA)*aMax);

                ip2.putPixel(c, r, average);
            }
        }
        img.show();
    }
}