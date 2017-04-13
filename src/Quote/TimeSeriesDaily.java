package Quote;

public class TimeSeriesDaily extends Quote {
    
    public double open;
    public double high;
    public double low;
    public double close;
    public double volume;
    
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
