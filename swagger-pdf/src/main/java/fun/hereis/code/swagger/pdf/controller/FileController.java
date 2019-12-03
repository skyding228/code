package fun.hereis.code.swagger.pdf.controller;

import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.apache.commons.io.FileUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.asciidoctor.Asciidoctor.Factory.create;

/**
 * @author weichunhe
 * created at 2019/11/18
 */
@RestController

public class FileController {
    private static Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Value("${baseDir:E:\\csv\\}")
    private String baseDir;

    @PostMapping("/file/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam(required = false, defaultValue = "false") boolean convert2Pdf) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        String fileName = file.getOriginalFilename();
        File dest = new File(baseDir + fileName);
        System.out.println(dest.getAbsolutePath());
        try {
            file.transferTo(dest);
            if (convert2Pdf) {
                pdf(dest.getPath());
            }
            LOGGER.info("上传成功");
            return "成功";
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return "上传失败！";
    }

    @GetMapping(value = "/file/download")
    public ResponseEntity<byte[]> download(String fileName) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        //解决乱码
        fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");

        headers.setContentDispositionFormData("attachment", fileName);
        //application/octet-stream ： 二进制流数据（最常见的文件下载）。
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        File dest = new File(baseDir + fileName);
        System.out.println("下载：" + dest.getAbsolutePath());
        InputStream in = new FileInputStream(dest);
        byte[] body = new byte[in.available()];
        in.read(body);
        return new ResponseEntity<byte[]>(body, headers, HttpStatus.CREATED);
    }


    private void copyFromClassPath(String folder) {
        File fonts = new File(baseDir + folder);
        if (!fonts.exists()) {
            try {
                File cls = new ClassPathResource(folder).getFile();
                FileUtils.copyDirectory(cls, fonts);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pdf(String file) {
        copyFromClassPath("fonts");
        copyFromClassPath("themes");
        //    输出Ascii格式
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .withOutputLanguage(Language.ZH)
//                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();
        // swagger-ui.html页面中能找到此链接
//        Swagger2MarkupConverter.from(new URL("http://localhost:"+serverPort+"/lottery/v2/api-docs?group=controller"))
        Swagger2MarkupConverter.from(Paths.get(file))
                .withConfig(config)
                .build()
                .toFile(Paths.get(file));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("source-highlighter", "coderay");
        attributes.put("imagesdir", "images@");
        attributes.put("attribute-missing", "skip");
        attributes.put("attribute-undefined", "drop-line");
        attributes.put("doctype", "book");
//        attributes.put("generated" , "/Users/swagger2pdf/swagger2pdf/swagger/asciidoc/generated");  // markup文件夹路径
        attributes.put("hardbreaks", "");
        attributes.put("icons", "font");
        attributes.put("numbered", "");
        attributes.put("pdf-fontsdir", baseDir + "fonts");  // 字体文件路径
        attributes.put("pdf-style", "KaiGenGothicCN");
        attributes.put("pdf-stylesdir", baseDir + "themes");  // 主题文件路径
        attributes.put("toc", "left");
        attributes.put("toclevels", "3");


        Options options = new Options();
        options.setAttributes(attributes); // (2)
        options.setInPlace(true); // (3)
        options.setBackend("pdf");

        Asciidoctor asciidoctor = create();
        String outfile = asciidoctor.convertFile(new File(file + ".adoc"), options);
        System.out.println(outfile);
    }


}
