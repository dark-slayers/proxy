package person.liuxx.proxy.source;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Objects;

import person.liuxx.proxy.business.ProxyAddress;



/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年9月5日 下午1:24:17
 * @since 1.0.0
 */
public class ProxySourceGoubanjia extends ProxySource
{
    public ProxySourceGoubanjia()
    {
        this.urls = new String[]
        { "http://www.goubanjia.com/index1.shtml", "http://www.goubanjia.com/index2.shtml",
                "http://www.goubanjia.com/index3.shtml", "http://www.goubanjia.com/index4.shtml",
                "http://www.goubanjia.com/index5.shtml",
                "http://www.goubanjia.com/free/index.shtml",
                "http://www.goubanjia.com/free/index2.shtml",
                "http://www.goubanjia.com/free/index3.shtml",
                "http://www.goubanjia.com/free/index4.shtml",
                "http://www.goubanjia.com/free/index5.shtml" };
        this.ipIndex = 0;
        this.portIndex = 1;
    }

    @Override
    public Elements parseTrs(Document doc)
    {
        Element table = doc.getElementById("list");
        return table.getElementsByTag("tr");
    }

    @Override
    protected Stream<ProxyAddress> trsToProxyAddressStream(Elements trs)
    {
        return trs.stream()
                .map(tr -> tr.getElementsByTag("td"))
                .filter(tds -> tds.size() > 1)
                .map(tds -> tds.get(0).getElementsByAttributeValueNot("style", "display: none;"))
                .map(spans ->
                {
                    String host = spans.stream()
                            .limit(spans.size() - 1)
                            .filter(e -> !Objects.equal(e.attr("style"), "display:none;"))
                            .filter(e -> !Objects.equal(e.tagName(), "td"))
                            .map(e -> e.text())
                            .collect(Collectors.joining(""));
                    int port = Integer.parseInt(spans.get(spans.size() - 1).text());
                    return new ProxyAddress(host, port);
                });
    }
}
