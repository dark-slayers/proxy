package person.liuxx.proxy.source;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月31日 下午4:47:25
 * @since 1.0.0
 */
public class ProxySource31f extends ProxySource
{
    public ProxySource31f()
    {
        this.urls = new String[]
        { "http://31f.cn/http-proxy/", "http://31f.cn/https-proxy/" };
        this.ipIndex = 1;
        this.portIndex = 2;
    }

    @Override
    public Elements parseTrs(Document doc)
    {
        Element table = doc.getElementsByClass("table table-striped").get(0);
        return table.getElementsByTag("tr");
    }
}
