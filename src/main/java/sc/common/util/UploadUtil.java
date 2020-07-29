package sc.common.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class UploadUtil {
	private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);
		
	public static ResultBean upload(MultipartFile mFile, String uploadPath) {
		String fileName = mFile.getOriginalFilename();
//		String fileName = UUID19.uuid();	// 重命名文件名称，防止重复
		try {
			// 创建文件存放路径实例
			File pFile = new File(uploadPath);
			// 判断文件夹不存在，则创建
			if (!pFile.exists()) {
				pFile.mkdirs();
			}
			// 创建文件实例
			File file = new File(uploadPath + fileName);
			// 判断文件已经存在，则删除该文件
	        if (file.exists()) {
	        	file.delete();
	        }
	        FileCopyUtils.copy(mFile.getBytes(), file);
	        logger.info("文件上传成功：" + fileName);
        	return ResultBean.success(fileName);
		} catch (Exception e) {
			logger.info("文件上传失败：" + fileName + "," + e.getMessage());
			return ResultBean.error(e.getMessage());
		}
	}
}
