package person.liuxx.proxy.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import person.liuxx.proxy.business.ProxyAddress;
import person.liuxx.proxy.util.HttpClientUtil;



/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月31日 下午3:49:34
 * @since 1.0.0
 */
public abstract class ProxySource
{
    protected Logger log = LogManager.getLogger();
    protected String[] urls;
    protected int ipIndex;
    protected int portIndex;

    public Stream<ProxyAddress> getProxyAddressStream()
    {
        Stream<String> stream = Stream.of(urls);
        return stream.flatMap(url -> parseUrl(url));
    }

    public static Stream<ProxySource> allSource()
    {
        List<ProxySource> list = new ArrayList<>();
        list.add(new ProxySource31f());
        list.add(new ProxySource66());
        list.add(new ProxySourceGoubanjia());
        list.add(new ProxySourceXiCi());
        return list.stream();
    }

    protected Stream<ProxyAddress> parseUrl(String url)
    {
        Optional<String> body = HttpClientUtil.simpleGet(url);
        Stream<ProxyAddress> result = body.map(b ->
        {
            Document doc = Jsoup.parse(b);
            Elements trs = parseTrs(doc);
            Stream<ProxyAddress> stream = trsToProxyAddressStream(trs);
            return stream;
        }).orElse(Stream.empty());
        return result;
    }

    protected abstract Elements parseTrs(Document doc);

    protected Stream<ProxyAddress> trsToProxyAddressStream(Elements trs)
    {
        return trs.stream()
                .map(tr -> tr.text())
                .map(l -> l.split(" "))
                .filter(a -> a[ipIndex].contains("."))
                .map(a ->
                {
                    String host = a[ipIndex];
                    int port = Integer.parseInt(a[portIndex]);
                    return new ProxyAddress(host, port);
                });
    }
}
