package os.component.upload.util;

import org.springframework.util.StringUtils;

/**
 *  文件上传工具类
 */
public class FileUploadUtils {

    public static boolean emptyAll(String...args) {
        for (int i = 0; i < args.length; i++) {
            if (!StringUtils.isEmpty(args[i])) {
                return false;
            }
        }
        return true;
    }
}
