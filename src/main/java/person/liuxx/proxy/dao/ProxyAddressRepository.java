package person.liuxx.proxy.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import person.liuxx.proxy.domain.ProxyAddressDO;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年9月4日 下午3:27:01
 * @since 1.0.0
 */
public interface ProxyAddressRepository extends JpaRepository<ProxyAddressDO, Long>
{
    Optional<ProxyAddressDO> findByHostAndPort(String host, int port);

    @Modifying
    @Transactional
    @Query("update ProxyAddressDO p set p.lastTestUrl=?1, p.lastUsableTime=?2, p.useTime=?3 where p.id=?4")
    int setLastTestUrlAndLastUsableTimeAndUseTimeById(String url, LocalDateTime time, Long useTime,
            Long id);
}
