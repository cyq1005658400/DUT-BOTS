package com.kob.matchingsystem.service;

import com.kob.matchingsystem.service.impl.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public static final MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating, Integer botId){
        System.out.println("Add Player: " + userId + " " + rating + " " + botId);
        matchingPool.addPlayer(userId, rating, botId);
        return "add player success";
    }
    @Override
    public String removePlayer(Integer userId){
        System.out.println("remove"+userId);
        matchingPool.removePlayer(userId);
        return  "remove player success";
    }
}
