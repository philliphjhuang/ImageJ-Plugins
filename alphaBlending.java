import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

public class alphaBlending implements PlugInFilter {
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
            for(int c = 0; c < column; c++){
                ip1.putPixel(c, r, ip.getPixel(c, r) + 50);
            }
        }
    }
}