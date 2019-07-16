package fun.hereis.code.spring;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * spel 表达式工具类
 *
 * @author weichunhe
 * created at 2019/2/26
 */
public class SpelUtil {

    private static Logger logger = LoggerFactory.getLogger(SpelUtil.class);
    private static Cache<String, Expression> spelCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(1, TimeUnit.HOURS).build();

    /**
     * 执行spel 表达式
     *
     * @param spel
     * @param context
     * @return
     */
    public static String exec(String spel, Object context) {
        try {
            Expression exp = spelCache.getIfPresent(spel);
            if (exp == null) {
                ExpressionParser parser = new SpelExpressionParser();
                exp = parser.parseExpression(spel);
                spelCache.put(spel, exp);
            }
            StandardEvaluationContext ctx = new StandardEvaluationContext(context);
            return exp.getValue(ctx, String.class);
        } catch (Exception e) {
            logger.error("spel error {}", spel, e);
        }
        return null;
    }

    /**
     * 执行spel 模板表达式
     *
     * @param spel
     * @param context
     * @return
     */
    public static String template(String spel, Object context) {
        try {
            Expression exp = spelCache.getIfPresent(spel);
            if (exp == null) {
                ExpressionParser parser = new SpelExpressionParser();
                exp = parser.parseExpression(spel,new TemplateParserContext());
                spelCache.put(spel, exp);
            }
            StandardEvaluationContext ctx = new StandardEvaluationContext(context);
            return exp.getValue(ctx, String.class);
        } catch (Exception e) {
            logger.error("spel template error {}", spel, e);
        }
        return null;
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("剩余", "100M");
        map.put("时间", "今天");
        System.out.println(template("还是#{ [''] == null ? ['时间'] : ['_formatted_']}", map));
        System.out.println(exec("T(java.lang.Float).valueOf('5.0')", map));
    }
}
