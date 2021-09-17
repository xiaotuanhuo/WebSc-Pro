package sc.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
/**
 * 提供操作数字的工具
 *
 * @author shq
 * @since 0.3.0
 */
public class NumberUtil {
	private final static DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance();
	
	/**
	 * 科学计数法转字符串
	 * @param number
	 * @return
	 */
	public static String format(double number) {
		DecimalFormat df = new DecimalFormat("0.00"); 
		df.setMaximumFractionDigits(8);
		return df.format(number);
	}
	
	/**
	 * 格式化数字为字符串。
	 * @param number 数字
	 * @param pattern 格式化参数
	 * @return 格式化的字符串
	 */
	public static String format(double number,String pattern){
		df.applyPattern(pattern);
		return df.format(number);
	}
	
	/**
	 * 四舍五入到指定的小数位数。
	 * <pre>
	 * 如：
	 * round(123.254,2) => 123.25
	 * round(123.257,2) => 123.26
	 * </pre>
	 * @param value 数值
	 * @param scale 小数位数
	 * @return 四舍五入后的数值
	 */
	public static double round(double value,int scale){
		return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 加法运算
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double add(double v1,double v2){
		BigDecimal dec1=new BigDecimal(Double.toString(v1));
		BigDecimal dec2=new BigDecimal(Double.toString(v2));
		return dec1.add(dec2).doubleValue();
	}
	
	/**
	 * 减法运算
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double sub(double v1,double v2){
		BigDecimal dec1=new BigDecimal(Double.toString(v1));
		BigDecimal dec2=new BigDecimal(Double.toString(v2));
		return dec1.subtract(dec2).doubleValue();
	}
	
	/**
	 * 乘法运算，精度由参数指定，四舍五入
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 */
	public static double mul(double v1,double v2,int scale){
		BigDecimal dec1=new BigDecimal(Double.toString(v1));
		BigDecimal dec2=new BigDecimal(Double.toString(v2));
		return div(dec1.multiply(dec2).doubleValue(),1,scale);
	}
	
	/**
	 * 乘法运算 默认精确到10位，四舍五入
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double mul(double v1,double v2){
		return mul(v1,v2,10);
	}
	
	/**
	 * 除法运算，精度由参数指定，四舍五入
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 */
	public static double div(double v1,double v2,int scale){
		if(scale<0)
           throw new IllegalArgumentException("小数精确位必须大于零"); 
		if(v2==0)
			throw new IllegalArgumentException("被除数不能为零");
		BigDecimal dec1=new BigDecimal(Double.toString(v1));
		BigDecimal dec2=new BigDecimal(Double.toString(v2));
		return dec1.divide(dec2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 除法运算，默认精确到10位，四舍五入
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double div(double v1,double v2){
		return div(v1,v2,10);
	}
}
