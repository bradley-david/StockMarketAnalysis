


public class TimeSeriesDaily extends Quote {

    double open;
    double high;
    double low;
    double close;
    double volume;
    
    public TimeSeriesDaily() {
    
    }
    
    @Override
    public String toString() {
        String ret = super.toString();
       
        ret += "\nOPEN: " + open;
        ret += "\nHIGH: " + high;
        ret += "\nLOW: " + low;
        ret += "\nCLOSE: " + close;
        ret += "\nVOLUME: " + volume;
        return ret;
    }
    
}
