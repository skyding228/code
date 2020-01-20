package fun.hereis.code.spring;

/**
 * 钉钉机器人
 *
 * @author weichunhe
 * created at 2020/1/20
 */
public class AliRobot {

    private static String url = System.getProperty("dingtalk", "https://oapi.dingtalk.com/robot/send?access_token=4c932e6309d3ffe9b92de6e69e293accaab8568a84c213ff4e2d3ec8d5c5868d");

    private static OkHttp okHttp = OkHttp.createInstanceWithProxy();

    private static AliRobot robot = new AliRobot();

    /**
     * 推送通知
     * @param text markdown通知内容，https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq/9e91d73c
     * @return 响应结果
     */
    public static Rsp notice(String text){
        return robot.send(text,"【通知】");
    }

    /**
     * 推送告警信息
     * @param text markdown通知内容，https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq/9e91d73c
     * @return 响应结果
     */
    public static Rsp warn(String text){
        return robot.send(text,"【告警】");
    }

    private Rsp send(String text,String title) {
        MarkdownMsg msg = new MarkdownMsg(text,title);
        System.out.println(JsonUtil.toJson(msg));
        Rsp rsp = okHttp.post(url, msg, Rsp.class);
        return rsp;
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

        public MarkdownMsg(String content,String title){
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

    public static class Rsp {
        /**
         * 0:成功
         */
        private Integer errcode;

        private String errmsg;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public boolean isSuccess(){
            return Integer.valueOf(0).equals(errcode);
        }
        @Override
        public String toString() {
            return "Rsp{" +
                    "errcode=" + errcode +
                    ", errmsg='" + errmsg + '\'' +
                    '}';
        }
    }

}
