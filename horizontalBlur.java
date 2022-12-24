import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

public class horizontalBlur implements PlugInFilter {
    public int setup(String arg, ImagePlus imp) {
        // TODO Auto-generated method stub
        if(arg.equals("about")){
            IJ.showMessage("Hi my name is Carmen Winstead");
            return DONE;
        } else {
            return DOES_ALL + NO_CHANGES;
        }
    }
    
    public void run(ImageProcessor ip) {
        int row = ip.getHeight();
        int column = ip.getWidth();

        ImagePlus img = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor ip1 = img.getProcessor();

        for(int r = 0; r < row; r++){
            for(int c = 3; c < column-3; c++){
                int pixel1 = ip.getPixel(c - 3, r);
                int pixel2 = ip.getPixel(c - 2, r);
                int pixel3 = ip.getPixel(c - 1, r);
                int pixel4 = ip.getPixel(c    , r);
                int pixel5 = ip.getPixel(c + 1, r);
                int pixel6 = ip.getPixel(c + 2, r);
                int pixel7 = ip.getPixel(c + 3, r);
                int pixelValue = (pixel1 + pixel2 + pixel3 + pixel4 + pixel5 + pixel6 + pixel7)/7;
                ip1.putPixel(c, r, pixelValue);
            }
        }

        //For edges
        for(int r = 0; r < row; r++){
            ip1.putPixelValue(0, r, (int)(1 + 1 + 1 + ip.getPixel(0, r) + ip.getPixel(1, r) + ip.getPixel(2, r) + ip.getPixel(3, r))/7 );
            ip1.putPixelValue(1, r, (int)(1 + 1 + ip.getPixel(0, r) + ip.getPixel(1, r) + ip.getPixel(2, r) + ip.getPixel(3, r) + ip.getPixel(4, r)) / 7);
            ip1.putPixelValue(2, r, (int)(1 + ip.getPixel(0, r) + ip.getPixel(1, r) + ip.getPixel(2, r) + ip.getPixel(3, r) + ip.getPixel(4, r) + ip.getPixel(5, r))/7);

            ip1.putPixelValue(column-1, r, (int)(ip.getPixel(column-4, r) + ip.getPixel(column-3, r) + ip.getPixel(column-2, r) + ip.getPixel(column-1, r) + 1 + 1 + 1)/7);
            ip1.putPixelValue(column-2, r, (int)(ip.getPixel(column-5, r) + ip.getPixel(column-4, r) + ip.getPixel(column-3, r) + ip.getPixel(column-2, r) + ip.getPixel(column-1, r) + 1 + 1)/7);
            ip1.putPixelValue(column-3, r, (int)(ip.getPixel(column-6, r) +ip.getPixel(column-5, r) +ip.getPixel(column-4, r) +ip.getPixel(column-3, r) +ip.getPixel(column-2, r) +ip.getPixel(column-1, r) + 1)/7);
        }
        img.show();
    }
}