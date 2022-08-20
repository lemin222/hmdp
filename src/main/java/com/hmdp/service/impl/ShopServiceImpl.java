package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmdp.constant.RedisConstants;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private ShopMapper shopMapper;

    @Override
    public Result queryShopById(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        //1.先从redis中获取
        String shopJson = redisTemplate.opsForValue().get(key);
        //2.如果redis中有数据
        if (!StringUtils.isEmpty(shopJson)) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        //3.如果redis没有数据,从mysql中获取
        Shop shop = shopMapper.selectById(id);
        if (shop == null) {
            //4.如果mysql中不存咋
            return Result.fail("店铺不存在");
        }
        //5.存在，则写入redis
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop));
        //6.返回
        return Result.ok(shop);

    }
}
