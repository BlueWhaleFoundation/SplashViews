package foundation.bluewhale.splashviews.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberTool {
    public static String convert(BigDecimal num) {
        BigDecimal result = num == null ? new BigDecimal(0) : num;
        Locale loc = Locale.getDefault();
        NumberFormat nf = NumberFormat.getInstance(loc);
        if(getNumberOfDecimalPlaces(result)>0)
            nf.setMinimumFractionDigits(2);
        return nf.format(result);
    }

    private static int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

    public static String convertInDetail(BigDecimal num) {
        return convert(num);
    }

    public static String convertWithCurrency(BigDecimal num, String currency) {
        if("BP".equals(currency)){
            return convert(num);
        }else
            return convertInDetail(num);
    }
}
