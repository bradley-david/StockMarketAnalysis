package Quote;

import java.util.Date;

public abstract class Quote {
    
    public String symbol;
    public Date date;
    
    @Override
    public String toString() {
        String ret = "\n\n" + getClass().getSimpleName();
        if (date != null) {
            ret += "\nDATE: " + date.toString();
        }
        return ret;
    }
}
