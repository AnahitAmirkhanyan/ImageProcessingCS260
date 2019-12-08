import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Cumulative_Histogram_HSV implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
        //ip.invert();
        Color color;

        // three arrays for H, S, and V channels

        int[] hist_h = new int[256];
        int[] hist_s = new int[256];
        int[] hist_v = new int[256];

        
        double r, g , b, h, s, v, cmax, cmin, diff;
        int h_i, s_i, v_i;
        h = 0;
        for(int i = 0; i < ip.getHeight(); i++){
            for(int j = 0; j < ip.getWidth(); j++){
                color = new Color(ip.getPixel(i,j));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();

                // convert RGB to HSV
                r = r/255.0;
                g = g/255.0;
                b = b/255.0;
                cmax = Math.max(Math.max(r, g), b);
                cmin = Math.min(Math.min(r, g), b);
                diff = cmax - cmin;
                
                if(cmax == cmin) h = 0;
                else if(cmax == r){
                    h = (60 * ((g - b) / diff) + 360) % 360;
                }
                else if(cmax == g){
                    h = (60 * ((b - r) / diff) + 120) % 360;
                }
                else if(cmax == b){
                    h = (60 * ((r - g) / diff) + 240) % 360;
                }

                if(cmax == 0){
                    s = 0;
                }
                else{
                    s = (diff / cmax) * 100;
                }

                v = cmax * 100;

                h_i = (int)Math.round(h); s_i = (int)Math.round(s); v_i = (int)Math.round(v);

                if(h_i > 255) h_i = 255;
                if(s_i > 255) s_i = 255;
                if(v_i > 255) v_i = 255;

                hist_h[h_i]++;
                hist_s[s_i]++;
                hist_v[v_i]++;

            }
        }

        int [] cumulative_h = new int[256];
        int [] cumulative_s = new int[256];
        int [] cumulative_v = new int[256];

        for(int i = 0; i < 256; i++){
            if(i == 0){
                cumulative_h[i] = hist_h[i];
                cumulative_s[i] = hist_s[i];
                cumulative_v[i] = hist_v[i];
            }
            else{
                cumulative_h[i] = cumulative_h[i-1] + hist_h[i];
                cumulative_s[i] = cumulative_s[i-1] + hist_s[i];
                cumulative_v[i] = cumulative_v[i-1] + hist_v[i];
            }
        }

        IJ.log("H");
        for(int i = 0; i < 256; i++){
            IJ.log(String.valueOf(cumulative_h[i]));
        }

        IJ.log("S");
        for(int i = 0; i < 256; i++){
            IJ.log(String.valueOf(cumulative_s[i]));
        }

        IJ.log("V");
        for(int i = 0; i < 256; i++){
            IJ.log(String.valueOf(cumulative_v[i]));
        }
	}

}