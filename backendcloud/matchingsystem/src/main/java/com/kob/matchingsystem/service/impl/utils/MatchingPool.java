package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// 匹配池是多线程的
@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private static final String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        // 在多个线程(匹配线程遍历players时，主线程调用方法时)会操作players变量，因此加锁
        lock.lock();
        try {
            players.add(new Player(userId, rating,  botId,0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {

        // 在多个线程(匹配线程遍历players时，主线程调用方法时)会操作players变量，因此加锁
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for(Player player : players) {
                if(!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime() {    // 将所有当前玩家的等待时间 + 1
        for(Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatched(Player a, Player b) {  // 判断两名玩家是否匹配
        // 获取两名分差
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        // min: 若取min则代表两者分差都小于 等待时间 * 10，实力差距最接近
        // max: 若取max则代表有一方分差小于 等待时间 * 10，实力差距可能会大
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    private void sendResult(Player a, Player b) {   // 返回匹配结果
        System.out.println("send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);

    }

    private void matchPlayers() {   // 尝试匹配所有玩家
        System.out.println("match players: " + players.toString());
        // 标记是否被匹配
        boolean[] used = new boolean[players.size()];

        // 先枚举等待最久的玩家，恰好是players前面的玩家等待的的久
        for(int i = 0; i < players.size(); i++) {
            if(used[i]) continue;
            // i只要和比i大的匹配，就正好所有玩家匹配一次
            for(int j = i + 1; j < players.size(); j++) {
                if(used[j]) continue;
                Player a = players.get(i), b = players.get(j);
                if(checkMatched(a, b)) {
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for(int i = 0; i < players.size(); i++) {
            if(!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                // 涉及到操作players变量，加锁；
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

