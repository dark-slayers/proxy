package person.liuxx.proxy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import person.liuxx.proxy.business.ProxyAddress;
import person.liuxx.proxy.service.ProxyService;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月29日 下午2:50:49
 * @since 1.0.0
 */
@RestController
@RequestMapping("/proxy")
@Api(value = "代理控制器")
public class ProxyController
{
    @Autowired
    private ProxyService proxyTestService;

    @ApiOperation(value = "获取代理地址列表的测试结果", notes = "获取测试代理后的测试结果")
    @GetMapping("/list")
    List<ProxyAddress> list()
    {
        return proxyTestService.listAddress();
    }
}
