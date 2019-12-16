import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Calibrate_HSV implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
    }
    
    public int[] rgbtohsv(double r, double g, double b){
        double h, s, v, cmax, cmin, diff;
        int h1, s1, v1;
        r = r/255.0;
        g = g/255.0;
        b = b/255.0;
        cmax = Math.max(Math.max(r, g), b);
        cmin = Math.min(Math.min(r, g), b);
        diff = cmax - cmin;
        h = 0;
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
        // array out of bounds because of rounding number
        if((int)Math.round(h) >= 360) h1 = 359;
        else h1 = (int)Math.round(h);

        if((int)Math.round(s) >= 100) s1 = 99;
        else s1 = (int)Math.round(s);

        if((int)Math.round(v) >= 100) v1 = 99;
        else v1 = (int)Math.round(v);

        int [] hsv = {h1, s1, v1};

        return hsv;
    }

    public int[] hsvtorgb(int hh, int ss, int vv){
        double h = hh/360.0;
        double s = ss/100.0;
        double v = vv/100.0;
        
        double r = 0; 
        double g = 0; 
        double b = 0;
    
        int i = (int)(Math.floor(h * 6));
        double f = h * 6 - i;
        double p = v * (1 - s);
        double q = v * (1 - f * s);
        double t = v * (1 - (1 - f) * s);
    
      switch (i % 6) {
        case 0: r = v; g = t; b = p; break;
        case 1: r = q; g = v; b = p; break;
        case 2: r = p; g = v; b = t; break;
        case 3: r = p; g = q; b = v; break;
        case 4: r = t; g = p; b = v; break;
        case 5: r = v; g = p; b = q; break;
      }
      
      int [] colors = {(int)Math.round(r*255), (int)Math.round(g*255), (int)Math.round(b*255)};
      return colors;
    }

	public void run(ImageProcessor ip) {
        //ip.invert();
        Color color;

        // benchmark histograms hardcoded
        double[] bm_h = {0.019316239,0.019818376,0.020694444,0.021965812,0.023760684,0.025886752,0.028044872,0.031527778,0.036623932,0.041217949,0.049487179,0.055470085,0.073557692,0.083226496,0.092510684,0.109850427,0.126068376,0.140886752,0.168226496,0.193696581,0.235224359,0.279594017,0.34073718,0.401527778,0.453418803,0.510972222,0.557938034,0.58715812,0.604690171,0.615854701,0.619775641,0.623301282,0.625438034,0.629252137,0.630320513,0.630747863,0.630897436,0.630982906,0.637211539,0.637222222,0.668717949,0.668717949,0.669391026,0.669391026,0.669423077,0.669455128,0.669455128,0.669455128,0.689807692,0.689807692,0.692393162,0.69241453,0.69241453,0.69241453,0.69241453,0.69241453,0.69241453,0.69241453,0.69241453,0.69241453,0.753055556,0.753055556,0.753055556,0.753055556,0.753055556,0.753055556,0.753055556,0.753055556,0.753066239,0.753066239,0.754123932,0.754123932,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767115385,0.767286325,0.767286325,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.767318376,0.76849359,0.76849359,0.76849359,0.768504274,0.768504274,0.768504274,0.768504274,0.768504274,0.768504274,0.768504274,0.76866453,0.76866453,0.76866453,0.76866453,0.76866453,0.76866453,0.76866453,0.76866453,0.76866453,0.76866453,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.768675214,0.769038462,0.769038462,0.769038462,0.769038462,0.769038462,0.769038462,0.769038462,0.769038462,0.769145299,0.769145299,0.769145299,0.769145299,0.769145299,0.769145299,0.769145299,0.769166667,0.769166667,0.769166667,0.773429487,0.773429487,0.773429487,0.773429487,0.773429487,0.773429487,0.773429487,0.773429487,0.773514957,0.773525641,0.774861111,0.774861111,0.774861111,0.774861111,0.774861111,0.778269231,0.778269231,0.778269231,0.778269231,0.778269231,0.77832265,0.77832265,0.77832265,0.780331197,0.780982906,0.780982906,0.780982906,0.780982906,0.780982906,0.780982906,0.788621795,0.788621795,0.788621795,0.788621795,0.788696581,0.790544872,0.790544872,0.790544872,0.790555556,0.791431624,0.799230769,0.799284188,0.817061966,0.817061966,0.817061966,0.817083333,0.847980769,0.847980769,0.847980769,0.848130342,0.848130342,0.851645299,0.862532051,0.87,0.87,0.87017094,0.880277778,0.920405983,0.934081197,0.934123932,0.934903846,0.934903846,0.936816239,0.950042735,0.950064103,0.950641026,0.950651709,0.957735043,0.96417735,0.964209402,0.968429487,0.968429487,0.974145299,0.974166667,0.974391026,0.974903846,0.974903846,0.974925214,0.977692308,0.977692308,0.979017094,0.979188034,0.979230769,0.979230769,0.979230769,0.979230769,0.979230769,0.979262821,0.979262821,0.979262821,0.983279915,0.983279915,0.983279915,0.983301282,0.983301282,0.983301282,0.983301282,0.983429487,0.983482906,0.983504274,0.984102564,0.984113248,0.985405983,0.985405983,0.985405983,0.985405983,0.985405983,0.985416667,0.985416667,0.985416667,0.985448718,0.985448718,0.985448718,0.985448718,0.985982906,0.985982906,0.986185897,0.986185897,0.986185897,0.986185897,0.986196581,0.986196581,0.986196581,0.986196581,0.986196581,0.986196581,0.986196581,0.986196581,0.986196581,0.986196581,0.988141026,0.988141026,0.988141026,0.988183761,0.988183761,0.988183761,0.988183761,0.988183761,0.988183761,0.988183761,0.988354701,0.988354701,0.988354701,0.988354701,0.988354701,0.988354701,0.988354701,0.988354701,0.988354701,0.988354701,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988365385,0.988846154,0.988846154,0.988846154,0.988846154,0.988846154,0.988846154,0.988846154,0.988846154,0.988899573,0.988899573,0.988899573,0.988899573,0.988899573,0.988899573,0.988899573,0.988910256,0.98892094,0.98892094,0.995384615,0.995384615,0.995395299,0.995395299,0.995395299,0.995395299,0.995395299,0.995395299,0.99542735,0.995438034,0.996217949,0.996217949,0.996217949,0.996217949,0.996228633,0.998600427,0.998600427,0.998707265,0.998760684,0.998771368,0.998856838,0.998856838,0.998867521,0.999326923,0.999476496,0.99948718,0.999561966,0.999626068,0.999797009,1};
        double[] bm_s = {0.016292735,0.017606838,0.02457265,0.03392094,0.071175214,0.13732906,0.188867521,0.214433761,0.228974359,0.236282051,0.244337607,0.254957265,0.264230769,0.28090812,0.294230769,0.305865385,0.311153846,0.322425214,0.332179487,0.3375,0.34633547,0.350683761,0.357382479,0.361891026,0.365309829,0.371730769,0.375651709,0.378290598,0.382916667,0.389305556,0.392799145,0.398376068,0.402115385,0.410769231,0.414433761,0.421655983,0.432884615,0.440459402,0.453450855,0.464273504,0.477222222,0.494903846,0.515,0.53542735,0.557147436,0.578087607,0.598418803,0.617179487,0.636837607,0.655769231,0.682083333,0.706388889,0.734102564,0.761794872,0.78642094,0.812232906,0.837307692,0.862136752,0.883536325,0.902211539,0.917863248,0.932777778,0.944423077,0.955288462,0.962884615,0.969615385,0.974764957,0.979316239,0.983173077,0.986410256,0.988792735,0.99099359,0.992831197,0.994262821,0.995534188,0.996570513,0.997297009,0.997799145,0.99823718,0.99857906,0.998878205,0.999134615,0.999326923,0.999508547,0.999615385,0.99974359,0.999839744,0.999903846,0.999925214,0.999925214,0.999967949,0.999967949,0.999967949,0.999978633,0.999978633,0.999978633,0.999989316,1,1,1};
        double[] bm_v = {0,0, 0.000331,0.004732906,0.024134615,0.057040598,0.081100427,0.111869658,0.13133547,0.15775641,0.172884615,0.190213675,0.198290598,0.208183761,0.214305556,0.22099359,0.226944444,0.230523504,0.235523504,0.23883547,0.244690171,0.249732906,0.25775641,0.264305556,0.274967949,0.284540598,0.291880342,0.30215812,0.30900641,0.319209402,0.326185897,0.335576923,0.341848291,0.351709402,0.358974359,0.369273504,0.378942308,0.385705128,0.396976496,0.405213675,0.419551282,0.430844017,0.449348291,0.463087607,0.484316239,0.503952992,0.517532051,0.54034188,0.556452992,0.579241453,0.594081197,0.618055556,0.635096154,0.659316239,0.674358974,0.695363248,0.714519231,0.727724359,0.750096154,0.769497863,0.796388889,0.810897436,0.827948718,0.836388889,0.847596154,0.857318376,0.863952992,0.877061966,0.886944444,0.90107906,0.909284188,0.920801282,0.927104701,0.937232906,0.947724359,0.968728633,0.991517094,0.997938034,0.999882479,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

        // I literally miss the sweet embrace of slumber

        double[] hist_h = new double[360];
        double[] hist_s = new double[100];
        double[] hist_v = new double[100];

        // compute hsv historgram of current image
        double r, g , b, h, s, v, cmax, cmin, diff;
        int[] hsv;
        h = 0;
        for(int i = 0; i < ip.getHeight(); i++){
            for(int j = 0; j < ip.getWidth(); j++){
                color = new Color(ip.getPixel(j,i));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                hsv = rgbtohsv(r, g, b);
                hist_h[hsv[0]]++;
                hist_s[hsv[1]]++;
                hist_v[hsv[2]]++;

            }
        }

        double [] cumulative_h = new double[360];
        double [] cumulative_s = new double[100];
        double [] cumulative_v = new double[100];

        for(int i = 0; i < 360; i++){
            if(i == 0) 
                cumulative_h[i] = hist_h[i];
            else
                cumulative_h[i] = cumulative_h[i-1] + hist_h[i];
        }

        
        for(int i = 0; i < 100; i++){
            if(i == 0){
                cumulative_s[i] = hist_s[i];
                cumulative_v[i] = hist_v[i];
            }
            else{
                cumulative_s[i] = cumulative_s[i-1] + hist_s[i];
                cumulative_v[i] = cumulative_v[i-1] + hist_v[i];
            }
        }

        double[] norm_cum_h = new double[360];
        double[] norm_cum_s = new double[100];
        double[] norm_cum_v = new double[100];

        for(int i = 0; i < 360; i++){
            norm_cum_h[i] = cumulative_h[i]/(ip.getWidth()*ip.getHeight());
        }

        for(int i = 0; i < 100; i++){
            norm_cum_s[i] = cumulative_s[i]/(ip.getWidth()*ip.getHeight());
            norm_cum_v[i] = cumulative_v[i]/(ip.getWidth()*ip.getHeight());
        }

        // now we shall embark upon teh calibration AT LONG LAST
        int h_act, s_act, v_act, h_new, s_new, v_new;
        for(int i = 0; i < ip.getHeight(); i++){
            for(int j = 0; j < ip.getWidth(); j++){
                color = new Color(ip.getPixel(j,i));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();

                 // convert RGB to HSV
                 // at this point i should have made it into a function since i am copy pasting the same thing for the third time
                 // i'm sorry

                hsv = rgbtohsv(r, g, b);
                h_act = hsv[0]; s_act = hsv[1]; v_act = hsv[2];

                h_new = 359;
                while(norm_cum_h[h_act] < bm_h[h_new]){
                    h_new--;
                    if(h_new == 0) break;
                }
                s_new = 99;
                while(norm_cum_s[s_act] < bm_s[s_new]){
                    s_new--;
                    if(s_new == 0) break;
                }
                v_new = 99;
                while(norm_cum_v[v_act] < bm_v[v_new]){
                    v_new--;
                    if(v_new == 0) break;
                }
                
                // have to convert back to rgb in order to putPixel as there seems to be no other way
                int[] colors = hsvtorgb(h_new, s_new, v_new);

                ip.putPixel(j,i, colors);


            }
        }

	}

}