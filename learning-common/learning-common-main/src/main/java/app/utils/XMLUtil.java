package app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created by lili19289 on 2016/8/31.
 */
public class XMLUtil {

    /**
     * @param xml
     *            只返回1层结构的键值，如果有嵌套，则合并子节点的text值（使用getStringValue()）
     * @return
     */
    public static Map<String, String> parseXML(String xml) {
        Map<String, String> params = new HashMap();
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            Iterator it = doc.getRootElement().elementIterator();
            while (it.hasNext()) {
                Element element = (Element) it.next();
                String name = element.getName();
                String text = element.getStringValue();
                params.put(name, text);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String convert2XML(Map<String, String> params) {
        StringBuffer sb = new StringBuffer("<xml>");
        try {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Entry) it.next();
                sb.append("<" + entry.getKey() + ">");
                // sb.append("<![CDATA[" + entry.getValue() + "]]");
                sb.append(entry.getValue());
                sb.append("</" + entry.getKey() + ">");
            }
            sb.append("</xml>");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String convert2XML_(Map<String, String> params) {
        StringBuffer sb = new StringBuffer("<xml>");
        try {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Entry) it.next();
                sb.append("<![CDATA[" + entry.getValue() + "]]");
                sb.append(entry.getValue());
                sb.append("</" + entry.getKey() + ">");
            }
            sb.append("<CreateTime>").append(new Date().getTime())
                    .append("</CreateTime>");
            sb.append("</xml>");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String inputStream2String(InputStream is) {
        StringBuffer buffer = new StringBuffer("");
        try {
            if (is.available() > 0) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(is));
                String line = "";
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
         String timestamp = "1392025824", signature =
         "61555c384bb850c25799b847b782ef81ae3e5f31", nonce = "1391656333";

         String xml =
         "<xml><ToUserName><![CDATA[gh_57383b5984c8]]></ToUserName>"
         +
         "<FromUserName><![CDATA[oUfqLt_klZDPOIXKn8jXc1VnTOWw]]></FromUserName>"
         + "<CreateTime>1396945226</CreateTime>"
         + "<MsgType><![CDATA[event]]></MsgType>"
         + "<Event><![CDATA[VIEW]]></Event>"
         +
         "<EventKey><![CDATA[http://122.227.129.183/MenuController/newGoods]]></EventKey>"
         + "</xml>";
         Map<String, String> map = XMLUtil
         .parseXML("<xml><OpenId><![CDATA[oDF3iY9P32sK_5GgYiRkjsCo45bk]]></OpenId><AppId><![CDATA[wxf8b4f85f3a794e77]]></AppId><TimeStamp>1393400471</TimeStamp><MsgType><![CDATA[request]]></MsgType><FeedBackId>7197417460812502768</FeedBackId> <TransId><![CDATA[1900000109201402143240185685]]></TransId><Reason><![CDATA[质量问题]]></Reason><Solution><![CDATA[换货]]></Solution><ExtInfo><![CDATA[备注 12435321321]]></ExtInfo><AppSignature><![CDATA[d60293982cc7c97a5a9d3383af761db763c07c86]]></AppSignature><SignMethod><![CDATA[sha1]]></SignMethod><PicInfo> <item><PicUrl><![CDATA[http://mmbiz.qpic.cn/mmbiz/49ogibiahRNtOk37iaztwmdgFbyFS9FU rqfodiaUAmxr4hOP34C6R4nGgebMalKuY3H35riaZ5vtzJh25tp7vBUwWxw/0]]></PicUrl></item><item><PicUrl><![CDATA[http://mmbiz.qpic.cn/mmbiz/49ogibiahRNtOk37iaztwmdgFbyFS9FUrqfn3y72eHKRSAwVz1PyIcUSjBrDzXAibTiaAdrTGb4eBFbib9ibFaSeic3OIg/0]]></PicUrl></item><item><PicUrl><![CDATA[]]></PicUrl></item><item><PicUrl><![CDATA[]]></PicUrl></item><item><PicUrl><![CDATA[]]></PicUrl></item></PicInfo></xml>");
         for (Map.Entry<String, String> e : map.entrySet()) {
         System.err.println(e.getKey() + ": " + e.getValue());
         }




    }
}
