package sc.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailTelValidateUtil {
	
	/**
	 * 邮箱格式校验
	 * @param mail 邮箱地址
	 * @return boolean
	 */
    public static boolean checkEmail(String mail) {
    	boolean flag = false;
        try{
            String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(mail);
            flag = matcher.matches();
            }catch(Exception e){
                flag = false;
            }
        return flag;
    }
    
    /**
     * <p>
     * 判断是否是手机号码
     * </p>
     * 
     * @param mobiles 手机号
     * @return boolean
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        if (mobiles.length() < 11) {
            return flag;
        } else {
            Pattern p = Pattern.compile("^[1](([3][0-9])|([4][5,7,9])|([5][2,4,6,9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        }
        return flag;
    }

	public static void main(String[] args) {
		
		System.out.println(checkEmail("rsbgsjd_sh@mail.notes.bank-of-china.com"));
//		System.out.println(isMobileNO("15221250002"));

	}

}
