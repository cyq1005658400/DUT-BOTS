package com.kob.botrunningsystem.service.Impl;

import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.service.Impl.utils.Botpool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public static final Botpool botPool = new Botpool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("add bot: " + userId + " " + botCode + " " + input);
        botPool.addBot(userId,botCode,input);
        return "add bot success";
    }
}
