package fun.hereis.code.spring;

import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.Placement;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.asciidoctor.Asciidoctor.Factory.create;

/**
 * 根据swagger 文件生成ascii 文档
 * @author weichunhe
 * created at 2019/11/11
 */
public class AsciiUtil {
    /**
     * 生成静态文件
     * @param swaggerUrl swagger 地址
     * @param outputDir 生成目录
     */
    public void staticFile(String swaggerUrl,String outputDir){
        URL remoteSwaggerFile = null;
        try {
            remoteSwaggerFile = new URL(swaggerUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path outputDirectory = Paths.get(outputDir);

        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
//                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                .withOutputLanguage(Language.ZH)
//                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(remoteSwaggerFile)
                .withConfig(config)
                .build();

        converter.toFile(outputDirectory);

        Attributes attributes = new Attributes();
        attributes.setCopyCss(true);

        attributes.setTableOfContents(Placement.LEFT);
        Options options = new Options();
        options.setCompact(true);
        options.setBackend("html");
        options.setDocType("book");
        options.setAttributes(attributes);
        options.setInPlace(true);
        Asciidoctor asciidoctor = create();
        asciidoctor.convertFile(outputDirectory.resolve("../swagger.adoc").toFile(), options);
    }


    public static void main(String[] args) throws MalformedURLException {
        URL remoteSwaggerFile = new URL("http://localhost:8080/lottery/v2/api-docs?group=controller");
        Path outputDirectory = Paths.get("build/swagger");

        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
//                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                .withOutputLanguage(Language.ZH)
//                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(remoteSwaggerFile)
                .withConfig(config)
                .build();

        converter.toFile(outputDirectory);

        Attributes attributes = new Attributes();
        attributes.setCopyCss(true);

        attributes.setTableOfContents(Placement.LEFT);
        Options options = new Options();
        options.setCompact(true);
        options.setBackend("html");
        options.setDocType("book");
        options.setAttributes(attributes); // (2)
        options.setInPlace(true); // (3)
        Asciidoctor asciidoctor = create();
//        System.out.println(asciidoctor.convert(converter.toString(), options));
        String outfile = asciidoctor.convertFile(outputDirectory.resolve("../swagger.adoc").toFile(), options);

    }
}
