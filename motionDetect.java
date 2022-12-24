import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;

public class motionDetect implements PlugInFilter {
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

        //Initialize
        int row = ip.getHeight();
        int column = ip.getWidth();
        List<String> titles = Arrays.asList(WindowManager.getImageTitles());
        String[] imageNames = {"soccer1.bmp", "soccer2.bmp"};
        ByteProcessor[] images = new ByteProcessor[imageNames.length];

        //Store all images in an array
        for(int i = 0; i < imageNames.length; i++){
            String imageName = imageNames[i];
            if(!titles.contains(imageName)){
                IJ.log("Does not contain the following image: " + imageName);
                return;
            } else{
                images[i] = (ByteProcessor)WindowManager.getImage(imageName).getProcessor();
            }
        }

        ImagePlus d_img = IJ.createImage("d", column, row, 1, 8);
        ImageProcessor d_ip = d_img.getProcessor();
        
        ImagePlus mask = IJ.createImage("Mask", column, row, 1, 8);
        ImageProcessor mask_ip = mask.getProcessor();
        int count = 0;

        //Get first and second image differnece
        for(int r = 0; r < row; r++){
            for(int c = 0; c < column; c++){

                //Get difference
                int difference = (int)Math.abs(images[0].getPixel(c, r) - images[1].getPixel(c,r));

                /* 
                //Increase contrast
                difference*=5;
                if(difference>255){
                    difference = 255;
                }
                */

                //Threshold
                //Threshold for 2% is about 24, 1% is about 37, 0.1% is about 125, 0.01% is about 166
                /*
                int threshold = 37; 
                if(difference>=threshold){
                    count++;
                    mask_ip.putPixel(c, r, 255);
                } else{
                    mask_ip.putPixelValue(c, r, 0);
                }
                */
                d_ip.putPixel(c, r, difference);
            }
        }

        //Threshold
        for(int r = 0; r < row; r++){
            for(int c = 0; c < column; c++){
                //Threshold
                //Threshold for 2% is about 24, 1% is about 37, 0.1% is about 125, 0.01% is about 166
                int threshold = 166; 
                if(d_ip.getPixel(c, r)>=threshold){
                    count++;
                    mask_ip.putPixel(c, r, 255);
                } else{
                    mask_ip.putPixelValue(c, r, 0);
                }
            }
        }
        /*
        // closing = dilation + erosion
        ImageProcessor ip_c = d_ip.duplicate();
        ip_c.dilate();
        ip_c.erode();
        */
        ImagePlus I_m = IJ.createImage("I_m", column, row, 1, 8);
        ImageProcessor I_m_ip = I_m.getProcessor();

        for(int r = 0; r < row; r++){
            for(int c = 0; c < column; c++){
                if(mask_ip.getPixel(c, r) == 255){
                    I_m_ip.putPixel(c, r, images[1].getPixel(c, r));
                } else{
                    I_m_ip.putPixel(c, r, 0);
                }
            }
        }
        mask.show();
        I_m.show();
        //IJ.log("Number of pixels: " + row*column + "\n Number of white pixels: " + count + " \np%~= " + (double)(count/(1.0*row*column)*100) + " %");
    }
}