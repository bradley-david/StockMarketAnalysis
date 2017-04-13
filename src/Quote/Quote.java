package Quote;

import java.util.Date;

public abstract class Quote {
    
    private String symbol;
    private Date date;
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        String ret = "\n\n" + getClass().getSimpleName();
        if (date != null) {
            ret += "\nDATE: " + date.toString();
        }
        return ret;
    }
}
