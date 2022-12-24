import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.util.Arrays;
import java.util.List;

public class motionBlur implements PlugInFilter {
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
        String[] imageNames = {"100.tif", "101.tif", "102.tif", "103.tif", "104.tif", "105.tif", "106.tif", "107.tif", "108.tif", "109.tif", "110.tif"};
        ByteProcessor[] images = new ByteProcessor[11];

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

        ImagePlus final_img = IJ.createImage(null, column, row, 1, 8);
        ImageProcessor final_ip = final_img.getProcessor();
        double alpha = 0.5;
        
        //For the first image
        for(int r = 0; r < row; r++){
            for(int c = 0; c < column; c++){
                final_ip.putPixel(c, r, images[0].getPixel(c, r));
            }
        }
        //Blend the rest
        for(int i = 1; i < images.length; i++){
            for(int r = 0; r < row; r++){
                for(int c = 0; c < column; c++){
                    int B = (int)(alpha*final_ip.getPixel(c, r) + (1 - alpha)*images[i].getPixel(c, r));
                    final_ip.putPixel(c, r, B);
                }
            }
        }
        final_img.show();
    }
}