package com.ruoyi.common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author xh
 */
public class FileReaderUtils {

    /**
     * 读取文件全部内容为字符串，使用指定编码
     */
    public static String readFileToString(File file, Charset charset) throws IOException {
        if (file == null) {
            throw new IOException("文件不能为空");
        }
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在或不是标准文件: " + file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            int bytesRead = fis.read(buffer);

            // 确保完整读取
            if (bytesRead != buffer.length) {
                throw new IOException("文件读取不完整，预期长度：" + buffer.length + "，实际读取：" + bytesRead);
            }

            return new String(buffer, charset);
        }
    }
}
