import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

public class brightEdges implements PlugInFilter {
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

        ImagePlus img = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor ip2 = img.getProcessor();

        //Every other pixels
        for(int r = 1; r < row-1; r++) {
            for(int c = 1; c < column-1; c++) {
                
                // -1 0 1
                // -2 0 2
                // -1 0 1
                double pixel1 = ip.getPixel(c-1, r-1);
                double pixel2 = ip.getPixel(c-1, r);
                double pixel3 = ip.getPixel(c-1, r+1);
                double pixel4 = ip.getPixel(c+1, r-1);
                double pixel5 = ip.getPixel(c+1, r);
                double pixel6 = ip.getPixel(c+1, r+1);
                int pixelValue1 = (int)(pixel4+2*pixel5+pixel6-pixel1-2*pixel2-pixel3);
                
                /*
                //-1 0 1
                double pixel1 = ip.getPixel(c-1, r);
                double pixel2 = ip.getPixel(c+1, r);
                int pixelValue = (int)(pixel1-pixel2);
                */

                // -1 -2 -1
                //  0  0  0
                //  1  2  1
                double pixel11 = ip.getPixel(c-1, r-1);
                double pixel22 = ip.getPixel(c, r-1);
                double pixel33 = ip.getPixel(c+1, r-1);
                double pixel44 = ip.getPixel(c-1, r+1);
                double pixel55 = ip.getPixel(c, r+1);
                double pixel66 = ip.getPixel(c+1, r+1);
                int pixelValue2 = (int)(pixel44+2*pixel55+pixel66-pixel11-2*pixel22-pixel33);


                //Gradient formula
                int gradient = (int)Math.sqrt(Math.pow(pixelValue1, 2) + Math.pow(pixelValue2, 2));
                ip2.putPixel(c, r, gradient);
            }
        }
        ImagePlus img2 = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor ip3 = img2.getProcessor();
        for(int r = 0; r < row; r++) {
            for(int c = 0; c < column; c++) {
                if(ip2.getPixel(c, r)==255){
                    ip3.putPixel(c, r, ip2.getPixel(c, r));
                } else{
                    ip3.putPixel(c, r, ip.getPixel(c, r));
                }
            }
        }
        img.show();
        img2.show();
    }
}