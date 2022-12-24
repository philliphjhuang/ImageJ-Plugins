import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

public class countColors implements PlugInFilter {
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
        int red = 0;
        int green = 0;
        int blue = 0;
        int white = 0;
        int black = 0;
        for(int r = 0; r < row; r++) {
            for(int c = 0; c < column; c++) {
                int pixel = ip.getPixel(c, r);
                int redValue = (pixel & 0xff0000) >> 16;
                int greenValue = (pixel & 0x00ff00) >> 8;
                int blueValue = (pixel & 0x0000ff);

                if (redValue == 255 && greenValue == 255 && blueValue == 255) {
                    white++;
                } else if (redValue == 255 && greenValue == 0 && blueValue == 0) {
                    red++;
                } else if (redValue == 0 && greenValue == 255 && blueValue == 0) {
                    green++;
                } else if (redValue == 0 && greenValue == 0 && blueValue == 255) {
                    blue++;
                } else if (redValue == 0 && greenValue == 0 && blueValue == 0) {
                    black++;
                }
            }
        }
        IJ.log("Number of pure red pixels: " + red);
        IJ.log("Number of pure green pixels: " + green);
        IJ.log("Number of pure blue pixels: " + blue);
        IJ.log("Number of pure white pixels: " + white);
        IJ.log("Number of pure black pixels: " + black);
    }
}