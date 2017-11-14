package person.liuxx.proxy.source;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年10月25日 下午3:29:42
 * @since 1.0.0
 */
public class ProxySourceXiCi extends ProxySource
{
    /**
     * 
     */
    public ProxySourceXiCi()
    {
        this.urls = new String[]
        { "http://www.xicidaili.com/nn/", "http://www.xicidaili.com/nt/",
                "http://www.xicidaili.com/wn/", "http://www.xicidaili.com/wt/" };
        this.ipIndex = 0;
        this.portIndex = 1;
    }

    @Override
    protected Elements parseTrs(Document doc)
    {
        Element table = doc.getElementById("ip_list");
        return table.getElementsByTag("tr");
    }
}
