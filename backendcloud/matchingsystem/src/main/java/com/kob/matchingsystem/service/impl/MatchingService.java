package com.kob.matchingsystem.service.impl;

public interface MatchingService {
    public String addPlayer(Integer userId,Integer rating, Integer botId);
    public String removePlayer(Integer userId);
}
