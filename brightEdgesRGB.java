import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

public class brightEdgesRGB implements PlugInFilter {
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
        // TODO Auto-generated method stub
        // Reading the image
        int row = ip.getHeight();
        int column = ip.getWidth();

        ImagePlus img_GrayScale = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor ip1 = img_GrayScale.getProcessor();

        //Turn the image into grayscale
        for(int r = 0; r < row; r++) {
            for(int c = 0; c < column; c++) {

                //Get RGB
                int pixel = ip.getPixel(c, r);
                int red = (pixel & 0xff0000) >> 16;
                int green = (pixel & 0x00ff00) >> 8;
                int blue = (pixel & 0x0000ff);

                //Grayscale
                int average = (red+blue+green)/3;

                ip1.putPixel(c, r, average);
            }
        }
        
        //Every pixel not on borders
        ImagePlus img_Gradient = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor ip2 = img_Gradient.getProcessor();
        for(int r = 1; r < row-1; r++) {
            for(int c = 1; c < column-1; c++) {
                
                // -1 0 1
                // -2 0 2
                // -1 0 1
                double pixel1 = ip1.getPixel(c-1, r-1);
                double pixel2 = ip1.getPixel(c-1, r);
                double pixel3 = ip1.getPixel(c-1, r+1);
                double pixel4 = ip1.getPixel(c+1, r-1);
                double pixel5 = ip1.getPixel(c+1, r);
                double pixel6 = ip1.getPixel(c+1, r+1);
                int pixelValue1 = (int)(pixel4+2*pixel5+pixel6-pixel1-2*pixel2-pixel3);
                
                // -1 -2 -1
                //  0  0  0
                //  1  2  1
                double pixel11 = ip1.getPixel(c-1, r-1);
                double pixel22 = ip1.getPixel(c, r-1);
                double pixel33 = ip1.getPixel(c+1, r-1);
                double pixel44 = ip1.getPixel(c-1, r+1);
                double pixel55 = ip1.getPixel(c, r+1);
                double pixel66 = ip1.getPixel(c+1, r+1);
                int pixelValue2 = (int)(pixel44+2*pixel55+pixel66-pixel11-2*pixel22-pixel33);
                
                /*
                // -0.5 0 0.5
                double pixel1 = ip1.getPixel(c-1, r);
                double pixel2 = ip1.getPixel(c, r);
                double pixel3 = ip1.getPixel(c+1, r);
                int pixelValue1 = (int)(-0.5*pixel1 + 0.5*pixel3);

                //-0.5
                //0
                //0.5
                double pixel11 = ip1.getPixel(c, r-1);
                double pixel22 = ip1.getPixel(c, r);
                double pixel33 = ip1.getPixel(c, r+1);
                int pixelValue2 = (int)(-0.5*pixel11 + 0.5*pixel33);
                */

                //Gradient formula
                int gradient = (int)Math.sqrt(Math.pow(pixelValue1, 2) + Math.pow(pixelValue2, 2));
                ip2.putPixel(c, r, gradient);
            }
        }
        ImagePlus img_RGB = IJ.createImage(null, column, row, 1, 24);
        ImageProcessor ip3 = img_RGB.getProcessor();
        for(int r = 0; r < row; r++) {
            for(int c = 0; c < column; c++) {
                if(ip2.getPixel(c, r)==255){
                    int RGB[] = {0, 0, 0};
                    ip3.putPixel(c, r, RGB);
                } else{
                    ip3.putPixel(c, r, ip.getPixel(c, r));
                }
            }
        }
        img_GrayScale.show();
        img_Gradient.show();
        img_RGB.show();
    }
}