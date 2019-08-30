package fun.hereis.code.spring;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author weichunhe
 * created at 19-8-17
 */
public class ClasspathPropertyUtil {
    /**
     * 加载类路径下面的properties
     * @param filename 类路径下面文件名
     * @return properties
     */
    public static Properties load(String filename) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = new ClassPathResource(filename).getInputStream();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
