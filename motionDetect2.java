import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;

public class motionDetect2 implements PlugInFilter {
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
        String[] imageNames = {"1015.jpg", "1020.jpg", "1025.jpg"};
        ColorProcessor[] imagesOriginal = new ColorProcessor[imageNames.length];
        ImagePlus[] images8bit = new ImagePlus[imageNames.length];
        ImageProcessor[] images8bitP = new ImageProcessor[imageNames.length];

        //Store all images in an array
        for(int i = 0; i < imageNames.length; i++){
            String imageName = imageNames[i];
            if(!titles.contains(imageName)){
                IJ.log("Does not contain the following image: " + imageName);
                return;
            } else{
                imagesOriginal[i] = (ColorProcessor)WindowManager.getImage(imageName).getProcessor();
            }
        }

        //Transform those RGB images into 8-bits
        for(int i = 0; i < imagesOriginal.length; i++){
            images8bit[i] = IJ.createImage(null, column, row, 1, 8);
            images8bitP[i] = images8bit[i].getProcessor();
            for(int r = 0; r < row; r++) {
                for(int c = 0; c < column; c++) {
                    //Get RGB
                    int pixel = imagesOriginal[i].getPixel(c, r);
                    int red = (pixel & 0xff0000) >> 16;
                    int green = (pixel & 0x00ff00) >> 8;
                    int blue = (pixel & 0x0000ff);
    
                    //Grayscale
                    int average = (red+blue+green)/3;
                    images8bitP[i].putPixel(c, r, average);
                }
            }
        }

        ImagePlus img1015_1020 = IJ.createImage("1015_1020", column, row, 1, 8);
        ImageProcessor ip1015_1020 = img1015_1020.getProcessor();

        ImagePlus img1020_1025 = IJ.createImage("1015_1020", column, row, 1, 8);
        ImageProcessor ip1020_1025 = img1020_1025.getProcessor();
        
        ImagePlus mask1015_1020 = IJ.createImage("Mask 1015_1020", column, row, 1, 8);
        ImageProcessor mask1_ip = mask1015_1020.getProcessor();

        ImagePlus mask1020_1025 = IJ.createImage("Mask 1015_1020", column, row, 1, 8);
        ImageProcessor mask2_ip = mask1020_1025.getProcessor();

        int count = 0;
        //Get first and second and third image differnece
        for(int r = 0; r < row; r++){
            for(int c = 0; c < column; c++){

                //Get difference
                int difference1 = (int)Math.abs(images8bitP[0].getPixel(c, r) - images8bitP[1].getPixel(c,r));
                int difference2 = (int)Math.abs(images8bitP[1].getPixel(c, r) - images8bitP[2].getPixel(c,r));
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
                ip1015_1020.putPixel(c, r, difference1);
                ip1020_1025.putPixel(c, r, difference2);
            }
        }

        ImagePlus combinedMask = IJ.createImage("Combined Mask", column, row, 1, 8);
        ImageProcessor ipCombinedMask = combinedMask.getProcessor();

        //Threshold
        for(int r = 0; r < row; r++){
            for(int c = 0; c < column; c++){
                //Threshold
                //Threshold for 2% is about 24, 1% is about 37, 0.1% is about 125, 0.01% is about 166
                int threshold = 166; 
                if(ip1015_1020.getPixel(c, r)>=threshold){
                    count++;
                    mask1_ip.putPixel(c, r, 255);
                } else{
                    mask1_ip.putPixelValue(c, r, 0);
                }

                if(ip1020_1025.getPixel(c, r)>=threshold){
                    count++;
                    mask2_ip.putPixel(c, r, 255);
                } else{
                    mask2_ip.putPixelValue(c, r, 0);
                }

                if((mask1_ip.getValue(c, r)==255)||(mask2_ip.getValue(c, r)==255)){
                    ipCombinedMask.putPixel(c, r, 255);
                } else {
                    ipCombinedMask.putPixel(c, r, 0);
                }

            }
        }
        /*
        // closing = dilation + erosion
        ImageProcessor ip_c = d_ip.duplicate();
        ip_c.dilate();
        ip_c.erode();
        */
        ImagePlus I_m = IJ.createImage("I_m", column, row, 1, 24);
        ImageProcessor I_m_ip = I_m.getProcessor();
        int[] white = {0, 0, 0};
        for(int r = 0; r < row; r++){
            for(int c = 0; c < column; c++){
                if(ipCombinedMask.getPixel(c, r) == 255){
                    I_m_ip.putPixel(c, r, imagesOriginal[1].getPixel(c, r));
                } else{
                    I_m_ip.putPixel(c, r, white);
                }
            }
        }
        combinedMask.show();
        I_m.show();
        //IJ.log("Number of pixels: " + row*column + "\n Number of white pixels: " + count + " \np%~= " + (double)(count/(1.0*row*column)*100) + " %");
    }
}