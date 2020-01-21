package fun.hereis.code.spring;

/**
 * 钉钉机器人
 *
 * @author weichunhe
 * created at 2020/1/20
 */
public class AliRobot {

    private String url = System.getProperty("dingtalk", "https://oapi.dingtalk.com/robot/send?access_token=4c932e6309d3ffe9b92de6e69e293accaab8568a84c213ff4e2d3ec8d5c5868d");

    private static OkHttp okHttp = OkHttp.createInstanceWithProxy();

    private static AliRobot robot = new AliRobot();

    /**
     * 推送通知
     *
     * @param text markdown通知内容，https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq/9e91d73c
     */
    public static void notice(String text) {
        robot.send(text, "【通知】");
    }

    /**
     * 推送告警信息
     *
     * @param text markdown通知内容，https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq/9e91d73c
     */
    public static void warn(String text) {
        robot.send(text, "【告警】");
    }

    /**
     * 发送消息
     *
     * @param text  markdown通知内容，https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq/9e91d73c
     * @param title 首屏会话透出的展示内容
     */
    public void send(String text, String title) {
        MarkdownMsg msg = new MarkdownMsg(text, title);
        System.out.println(JsonUtil.toJson(msg));
        okHttp.asyncPost(url, msg, OkHttp.defaultCallBack);
    }

    public static void main(String[] args) {
        warn("测试结果");
    }

    public class Markdown {
        private String title;
        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public class MarkdownMsg {
        private String msgtype = "markdown";
        private Markdown markdown;

        public MarkdownMsg(String content, String title) {
            markdown = new Markdown();
            markdown.text = content;
            markdown.title = title;
        }

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public Markdown getMarkdown() {
            return markdown;
        }

        public void setMarkdown(Markdown markdown) {
            this.markdown = markdown;
        }
    }


}
