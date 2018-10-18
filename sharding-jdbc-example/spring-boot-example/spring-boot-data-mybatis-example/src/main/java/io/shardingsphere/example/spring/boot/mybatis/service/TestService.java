package io.shardingsphere.example.spring.boot.mybatis.service;

import io.shardingsphere.example.spring.boot.mybatis.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by wuyu on 2018/10/17.
 */
@Service
public class TestService {

    private static final Logger log = LoggerFactory.getLogger(TestService.class);

    @Resource
    private OrderRepository orderRepository;
    /** 循环查询*/
    public void selectByCycle(){

        while (true){

            int cityId1 = new Random().nextInt(200)*2+1;
            int cityId2 = new Random().nextInt(200)*2;

            try {
                log.info("cityId1={},cityId2={}",cityId1,cityId2);
                List<Integer> list = orderRepository.selectByBatch(Arrays.asList(cityId1,cityId2));
                log.info("list={}",list);
                Thread.sleep(100L);
            }catch (Exception e){
                log.error("cityId1={},cityId2={}",cityId1,cityId2,e);
                break;
            }
        }
    }
}
