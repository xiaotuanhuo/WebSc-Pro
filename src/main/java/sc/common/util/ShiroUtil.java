package sc.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.stereotype.Component;

import sc.system.model.WebScUser;
import sun.misc.BASE64Encoder;

@Component
public class ShiroUtil {

    public static final String USER_LOCK = "0";

    /**
     * 获取当前登录用户.
     */
    public static WebScUser getCurrentUser() {
    	WebScUser user = (WebScUser) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            throw new UnauthenticatedException("未登录被拦截");
        }
        return user;
    }
    
    /**
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author: 
     * @CreateTime: 
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String getImageStr(String imgFile) throws UnsupportedEncodingException {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		// 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }
}
