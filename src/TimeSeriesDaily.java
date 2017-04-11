
import java.util.Date;

/**
 * Created by David on 4/10/2017.
 */
public class TimeSeriesDaily extends Quote {
    Date date;
    double open;
    double high;
    double low;
    double close;
    double volume;
    
    public TimeSeriesDaily() {
    
    }
    
    @Override
    public String toString() {
        String ret = "\n\nTIME_SERIES_DAILY data point: ";
        if (date != null) {
            ret += "\nDATE: " + date.toString();
        }
        ret += "\nOPEN: " + open;
        ret += "\nHIGH: " + high;
        ret += "\nLOW: " + low;
        ret += "\nCLOSE: " + close;
        ret += "\nVOLUME: " + volume;
        return ret;
    }
    
}
