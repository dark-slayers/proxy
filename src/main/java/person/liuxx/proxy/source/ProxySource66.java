package person.liuxx.proxy.source;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年9月1日 下午3:47:06
 * @since 1.0.0
 */
public class ProxySource66 extends ProxySource
{
    public ProxySource66()
    {
        this.urls = new String[]
        { "http://www.66ip.cn/index.html", "http://www.66ip.cn/2.html", "http://www.66ip.cn/3.html",
                "http://www.66ip.cn/4.html", "http://www.66ip.cn/5.html" };
        this.ipIndex = 0;
        this.portIndex = 1;
    }

    @Override
    public Elements parseTrs(Document doc)
    {
        Element table = doc.getElementsByClass("containerbox boxindex")
                .get(0)
                .getElementsByTag("div")
                .get(0)
                .getElementsByTag("table")
                .get(0);
        return table.getElementsByTag("tr");
    }
}
