package fun.hereis.code.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author weichunhe
 * created at 2019/10/23
 */
@SupportedAnnotationTypes({"*"})
public class MyAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("11111111111111111111111111111111111111111111");
        log("11111111111======================");
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        System.out.println("222222222222222222222222222222222222");
        log("2222222222======================");
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        System.out.println("=========================================");
        log("33333333======================");
        System.out.println(env);
    }

    private void log(String msg) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
    }

    public static void main(String[] args) {
        System.out.println("build");
    }
}
