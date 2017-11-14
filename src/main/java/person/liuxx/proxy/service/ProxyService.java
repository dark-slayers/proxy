package person.liuxx.proxy.service;

import java.util.List;

import person.liuxx.proxy.business.ProxyAddress;



/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月30日 下午1:58:18
 * @since 1.0.0
 */
public interface ProxyService
{
    /**
     * 获取可用的代理地址列表
     * 
     * @author 刘湘湘
     * @version 1.0.0<br>
     *          创建时间：2017年9月6日 上午9:46:43
     * @since 1.0.0
     * @return
     */
    List<ProxyAddress> listAddress();

    /**
     * 重新从代理网站获取最新的代理列表
     * 
     * @author 刘湘湘
     * @version 1.0.0<br>
     *          创建时间：2017年9月6日 上午9:44:18
     * @since 1.0.0
     */
    void flushAddressList();

    /**
     * 对缓存的代理列表进行验证
     * 
     * @author 刘湘湘
     * @version 1.0.0<br>
     *          创建时间：2017年9月4日 下午4:19:26
     * @since 1.0.0
     */
    void testCacheAddressList();

    /**
     * 对数据库的代理列表进行验证
     * 
     * @author 刘湘湘
     * @version 1.0.0<br>
     *          创建时间：2017年9月6日 上午9:48:22
     * @since 1.0.0
     */
    void testDatabaseAddressList();
}
