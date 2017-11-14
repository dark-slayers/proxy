package person.liuxx.proxy.source;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.hc.client5.http.config.AuthSchemes;
import org.apache.hc.client5.http.config.CookieSpecs;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.sync.methods.HttpGet;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.ResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import person.liuxx.proxy.business.ProxyAddress;
import person.liuxx.util.base.StringUtil;
import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年9月18日 下午2:48:55
 * @since 1.0.0
 */
public class ProxySourceKuai extends ProxySource
{
    private static String cookie;
    private static final long TIME_OUT = 15;

    public ProxySourceKuai()
    {
        this.urls = new String[]
        { "http://www.kuaidaili.com/ops/proxylist/1/", "http://www.kuaidaili.com/ops/proxylist/2/",
                "http://www.kuaidaili.com/ops/proxylist/3/",
                "http://www.kuaidaili.com/ops/proxylist/4/",
                "http://www.kuaidaili.com/ops/proxylist/5/",
                "http://www.kuaidaili.com/free/inha/1/", "http://www.kuaidaili.com/free/inha/2/",
                "http://www.kuaidaili.com/free/inha/3/", "http://www.kuaidaili.com/free/inha/4/",
                "http://www.kuaidaili.com/free/inha/5/", "http://www.kuaidaili.com/free/intr/1/",
                "http://www.kuaidaili.com/free/intr/2/", "http://www.kuaidaili.com/free/intr/3/",
                "http://www.kuaidaili.com/free/intr/4/", "http://www.kuaidaili.com/free/intr/5/", };
        this.ipIndex = 0;
        this.portIndex = 1;
    }

    @Override
    protected Elements parseTrs(Document doc)
    {
        Elements temp = doc.getElementsByClass("table table-bordered table-striped");
        Element table = null;
        if (temp.size() > 0)
        {
            table = temp.get(0);
        } else
        {
            table = doc.getElementsByClass("table table-b table-bordered table-striped").get(2);
        }
        Elements trs = table.getElementsByTag("tr");
        return trs;
    }

    @Override
    protected Stream<ProxyAddress> parseUrl(String url)
    {
        Optional<String> body = cookieGet(url);
        Stream<ProxyAddress> result = body.map(b ->
        {
            Document doc = Jsoup.parse(b);
            Elements trs = parseTrs(doc);
            Stream<ProxyAddress> stream = trsToProxyAddressStream(trs);
            return stream;
        }).orElse(Stream.empty());
        return result;
    }

    private Optional<String> cookieGet(String url)
    {
        try (CloseableHttpClient httpclient = HttpClients.createDefault())
        {
            final HttpGet httpget = new HttpGet(url);
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.STANDARD)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM,
                            AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .build();
            RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                    .setSocketTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .setConnectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .setConnectionRequestTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();
            httpget.setConfig(requestConfig);
            httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/60.0.3112.78 Safari/537.36");
            httpget.addHeader("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            httpget.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpget.addHeader("Accept-Encoding", "gzip, deflate");
            if (!StringUtil.isEmpty(cookie))
            {
                httpget.addHeader("Cookie", cookie);
            }
            log.info("Executing request {} -> {}", httpget.getMethod(), httpget.getUri());
            final ResponseHandler<Optional<String>> responseHandler = new CookieResponseHandler();
            Optional<String> op = httpclient.execute(httpget, responseHandler);
            if (op.isPresent())
            {
                return op;
            }
            httpget.addHeader("Cookie", cookie);
            Optional<String> op2 = httpclient.execute(httpget, responseHandler);
            if (op2.isPresent())
            {
                return op2;
            }
        } catch (IOException | URISyntaxException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
        log.warn("无法获取服务器的正确响应！");
        return Optional.empty();
    }

    class CookieResponseHandler implements ResponseHandler<Optional<String>>
    {
        @Override
        public Optional<String> handleResponse(ClassicHttpResponse response) throws HttpException,
                IOException
        {
            final int status = response.getCode();
            if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION)
            {
                final HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
                return Optional.ofNullable(body);
            } else
            {
                log.warn("服务器响应码：{}", status);
                final HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity) : null;
                Script script = new Script(body);
                if (response.containsHeader("Set-Cookie"))
                {
                    cookie = response.getSingleHeader("Set-Cookie").getValue().split(";")[0] + "; "
                            + script.getA().getCookie();
                } else
                {
                    log.warn("服务器响应码：{},无法设置cookie！", status);
                }
                return Optional.empty();
            }
        }
    }
}

class A
{
    int[] array;
    int q1;
    int q2;
    int q3;
    int q4;
    int q5;
    int q6;
    int q7;
    int q8;
    int q9;
    int q10;
    int q11;
    int q12;
    int q13;
    int q14;

    String r()
    {
        int qo = q2;
        do
        {
            array[qo] = (-array[qo]) & 0xff;
            array[qo] = (((array[qo] >> q3) | ((array[qo] << q4) & 0xff)) - q5) & 0xff;
        } while (--qo >= 2);
        qo = q6;
        do
        {
            array[qo] = (array[qo] - array[qo - 1]) & 0xff;
        } while (--qo >= 3);
        qo = 1;
        for (;;)
        {
            if (qo > q7)
                break;
            array[qo] = ((((((array[qo] + q8) & 0xff) + q9) & 0xff) << q10) & 0xff) | (((((array[qo]
                    + q11) & 0xff) + q12) & 0xff) >> q13);
            qo++;
        }
        String po = "";
        for (qo = 1; qo < array.length - 1; qo++)
        {
            if (qo % q14 != 0)
                po += String.valueOf((char) (array[qo] ^ q1));
        }
        return po;
    }

    String getCookie()
    {
        String s = r();
        String cookie = s.split(";")[0].substring(17);
        return cookie;
    }

    @Override
    public String toString()
    {
        return "A [array=" + Arrays.toString(array) + ", q1=" + q1 + ", q2=" + q2 + ", q3=" + q3
                + ", q4=" + q4 + ", q5=" + q5 + ", q6=" + q6 + ", q7=" + q7 + ", q8=" + q8 + ", q9="
                + q9 + ", q10=" + q10 + ", q11=" + q11 + ", q12=" + q12 + ", q13=" + q13 + ", q14="
                + q14 + "]";
    }
}

class Script
{
    private String script;
    private String text;
    private Logger log = LogManager.getLogger();

    /**
     * @param group
     */
    public Script(String script)
    {
        this.script = script;
    }

    A getA()
    {
        log.debug("script:{}", script);
        A a = new A();
        int endIndex = script.indexOf(")\",");
        text = script.substring(0, endIndex).trim();
        int beginIndex = text.lastIndexOf("(");
        text = text.substring(beginIndex + 1).trim();
        a.q1 = Integer.valueOf(text);
        text = script.substring(endIndex).trim();
        beginIndex = text.indexOf("oo = [");
        text = text.substring(beginIndex + 6).trim();
        endIndex = text.indexOf("];");
        String arrayText = text.substring(0, endIndex).trim();
        String[] textArray = arrayText.split(",");
        a.array = new int[textArray.length];
        for (int i = 0, max = textArray.length; i < max; i++)
        {
            a.array[i] = Integer.valueOf(textArray[i].substring(2).trim(), 16);
        }
        text = text.substring(endIndex + 1).trim();
        a.q2 = getNumber("\"qo=", ";");
        a.q3 = getNumber(">>", ")");
        a.q4 = getNumber("<<", ")");
        a.q5 = getNumber("))-", ")");
        a.q6 = getNumber("eval(qo);qo =", ";");
        a.q7 = getNumber("if (qo >", ")");
        a.q8 = getNumber("(oo[qo] +", ")");
        a.q9 = getNumber("0xff) +", ")");
        a.q10 = getNumber("<<", ")");
        a.q11 = getNumber("(oo[qo] +", ")");
        a.q12 = getNumber("0xff) +", ")");
        a.q13 = getNumber(">>", ")");
        a.q14 = getNumber("%", ")");
        return a;
    }

    int getNumber(String begin, String end)
    {
        int beginIndex = text.indexOf(begin);
        text = text.substring(beginIndex + begin.length()).trim();
        int endIndex = text.indexOf(end);
        return Integer.valueOf(text.substring(0, endIndex).trim());
    }
}