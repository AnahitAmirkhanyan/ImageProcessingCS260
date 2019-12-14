import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Cumulative_Histogram_RGB implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
        //ip.invert();
        Color color;
        // three arrays for R G B histograms 
        double histogram_red[] = new double[256];
        double histogram_green[] = new double[256];
        double histogram_blue[] = new double[256];
        int r, g, b;
        for(int i = 0; i < ip.getHeight(); i++){
            for(int j = 0; j < ip.getWidth(); j++){
                color = new Color(ip.getPixel(j,i));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                histogram_red[r]++;
                histogram_green[g]++;
                histogram_blue[b]++;

            }
        }

        double [] cumulative_red = new double[256];
        double [] cumulative_green = new double[256];
        double [] cumulative_blue = new double[256];

        for(int i = 0; i < 256; i++){
            if(i == 0){
                cumulative_red[i] = histogram_red[i];
                cumulative_green[i] = histogram_green[i];
                cumulative_blue[i] = histogram_blue[i];
            }
            else{
                cumulative_red[i] = cumulative_red[i-1] + histogram_red[i];
                cumulative_green[i] = cumulative_green[i-1] + histogram_green[i];
                cumulative_blue[i] = cumulative_blue[i-1] + histogram_blue[i];
            }
        }

        double[] norm_cum_red = new double[256];
        double[] norm_cum_green = new double[256];
        double[] norm_cum_blue = new double[256];

        for(int i = 0; i < 256; i++){
            norm_cum_red[i] = cumulative_red[i]/(ip.getHeight()*ip.getWidth());
            norm_cum_green[i] = cumulative_green[i]/(ip.getHeight()*ip.getWidth());
            norm_cum_blue[i] = cumulative_blue[i]/(ip.getHeight()*ip.getWidth());
        }

        IJ.log("RED");
        for(int i = 0; i < 256; i++){
            IJ.log(String.valueOf(norm_cum_red[i]));
        }

        IJ.log("GREEN");
        for(int i = 0; i < 256; i++){
            IJ.log(String.valueOf(norm_cum_green[i]));
        }

        IJ.log("BLUE");
        for(int i = 0; i < 256; i++){
            IJ.log(String.valueOf(norm_cum_blue[i]));
        }
	}

}