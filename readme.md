# 大连理工大学1024程序员节Bot对战平台DUT BOTS

基于springboot+vue实现基于微服务的bot对战平台

## 玩法介绍

#### 用户按照给出的代码模板编写ai，ai与ai对战下棋

## 快速上手

### 前置依赖

- Java 8
- Mysql
- Vue
- Nginx
- Docker

### 使用方法

#### Linux

##### 1.更改 Java Spring的配置文件

```java
server.port=3000 //后端服务端口号

spring.datasource.username=root
spring.datasource.password=123456 //修改为您自己的mysql的密码
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/kob?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```



##### 2.更改前端api接口

```javascript
const refresh_bots = () => {
    $.ajax({
        url: "http://127.0.0.1:3000/api/user/bot/getlist/",//将所有View中的127.0.0.1改为您服务器的地址
        type: "get",
        headers: {
            Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
            bots.value = resp;
        }
    })
}
```



##### 3.打包

Java部分打包

```
idea中打开右侧Maven，点开backendcloud -> 生命周期 -> 双击clean -> 双击package
```

Vue部分打包

```
打开vue脚手架，在任务界面选择build，运行
```

##### 

##### 4.配置nginx

```nginx
user www-data;
worker_processes auto;
pid /run/nginx.pid;
include /etc/nginx/modules-enabled/*.conf;

events {
    worker_connections 768;
    # multi_accept on;
}

http {

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    access_log /var/log/nginx/access.log;
    error_log /var/log/nginx/error.log;

    gzip on;

    include /etc/nginx/conf.d/*.conf;
    include /etc/nginx/sites-enabled/*;

    server {
        listen 8000; //前端访问的端口号
        server_name 127.0.0.1;//改为您的ip地址！！！！！

        charset utf-8;
        access_log /var/log/nginx/access.log;
        error_log /var/log/nginx/error.log;

        client_max_body_size 10M;

        location /api {
            proxy_pass http://127.0.0.1:3000;
        }

        location /websocket {
            proxy_pass http://127.0.0.1:3000;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header Host $http_host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_read_timeout  36000s;
        }

        location / {
            root /home/root/DUT-BOTS/web;//改为您的文件路径！！！！
            index index.html;
            try_files $uri $uri/ /index.html;
        }
```



##### 5.运行

- 启动Nginx

  ```bash
  sudo /etc/init.d/nginx start
  ```

  在浏览器中输入域名测试，若提示403 forbidden则表示成功，并测试放行端口显示405

  

- 启动后端

```
java -jar backend-0.0.1-SNAPSHOT.jar
java -jar matchingsystem-0.0.1-SNAPSHOT.jar
java -jar botrunningsystem-0.0.1-SNAPSHOT.jar
```

***访问ip地址:8000 就可以访问前端网页了！***



## 网站预览：

### 首页：

![](https://s3.bmp.ovh/imgs/2023/11/24/579dd26d23f87b96.png)



### 天梯排行榜（学号等敏感信息已打🐎）:

![](https://s3.bmp.ovh/imgs/2023/11/24/bd444c0f3ecf2631.png)





### 游戏内容：

#### **五子棋**

游戏规则
这是一个简单的五子棋游戏，属于回合制游戏。该游戏为双人对战模式，每一回合需要选择一个位置下棋。

![](https://s3.bmp.ovh/imgs/2023/11/24/c61b8aed3432f8fe.png)



**示例ai代码**

编写者可以修改next（）函数中的内容

```C++
#include<iostream>
#include<vector>
#include<queue>
#include<time.h>
#include<stdlib.h>
using namespace std;
// 存储五子棋棋子(x, y)
typedef pair<int, int> PII;
// 棋盘大小
const int rows = 17, cols = 17;
// 代表上、右上、右、右下、下、左下、左、左上八个方向的方向向量
int dx[] = {-1, -1, 0, 1, 1, 1, 0, -1}, dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
// 棋盘(值为0表示可下，1表示我方棋子，2表示敌方棋子)
int g[rows][cols] = {0};
// 分别代表我方和敌方棋子个数
int as, bs;
// 分别代表我方和敌方棋子
vector<PII> a, b;

// 由你来编写
// 返回PII类型的棋子坐标
PII next() {
    // 示例：
    // 策略：找出我方最长连线棋子，为了预防超时，当有两个位置可以下则找这两个位置中最长连线直接下子
    // 通过大根堆来存储我可以下的位置和对应的棋子个数，键为棋子个数，值为ps数组下标对应的棋子坐标
    priority_queue<PII, vector<PII>, less<PII> > heap;
    // 我方可下的位置
    vector<PII> ps;
    // 遍历棋盘
    for(int i = 1; i < rows - 1; i++) {
        for(int j = 1; j < cols - 1; j++) {
            // 若(i, j)是我方棋子
            if(g[i][j] == 1) {
                // 遍历8个方向
                for(int k = 0; k < 8; k++) {
                    int x = i + dx[k], y = j + dy[k];
                    int cnt = 0;
                    // 统计方向k有多少个连续的我方棋子
                    while(x >= 1 && x < rows - 1 && y >= 1 && y < cols - 1 && g[x][y] == 1) {
                        cnt++;
                        x += dx[k], y += dx[k];
                    }
                    // 若另一边没有下，则将对应的棋子个数和棋子坐标存储
                    if(g[x][y] == 0) {
                        heap.push({cnt, ps.size()});
                        ps.push_back({x, y});
                    }
                    // 防止复杂度太高，出现本回合无操作情况，当有2个位置可以下子时，则直接操作
                    if(heap.size() >= 2) {
                        int t = heap.top().second;
                        return ps[t];
                    }
                }
            }
        }
    }
    // 若无地可下，则随机坐标下棋
    if(heap.empty()) {
        srand((unsigned)time(NULL));
        int a = (rand() % (rows - 1 - 1 + 1)) + 1, b = (rand() % (cols - 1 - 1 + 1)) + 1;
        while(g[a][b] != 0) {
            a = (rand() % (rows - 1 - 1 + 1)) + 1, b = (rand() % (cols - 1 - 1 + 1)) + 1;
        }
        return {a, b};
    } else {
        // 下最多连续棋子的地方
        int t = heap.top().second;
        return ps[t];
    }
}

int main() {
    // 获取我方棋子的个数和位置
    cin >> as;
    for(int i = 0; i < as; i++) {
        int x, y;
        cin >> x >> y;
        a.push_back({x, y});
        // 标记棋盘
        g[x][y] = 1;
    }
    
    // 获取敌方棋子的个数和位置
    cin >> bs;
    for(int i = 0; i < bs; i++) {
        int x, y;
        cin >> x >> y;
        b.push_back({x, y});
        // 标记棋盘
        g[x][y] = 2;
    }
    PII t = next();
    cout << t.first << " " << t.second << endl;
    return 0;
}
```



#### **黑白棋**

游戏规则
[百度解释](https://baike.baidu.com/item/黑白棋/80689), 这是一个有趣简单的游戏，但是极其难精通，它属于回合制游戏。该游戏为双人对战模式，每一回合需要选择一个位置，此位置在横、竖、斜八个方向内有一个自己的棋子，则被夹在中间的全部翻转会成为自己的棋子。并且，只有在可以翻转棋子的地方才可以下子。



![](https://s3.bmp.ovh/imgs/2023/11/24/5cc77ed417df3670.png)

**示例ai代码：**

编写者可以修改next（）函数中的内容

```c++
#include<iostream>
#include<vector>
#include<queue>
using namespace std;
// 存储黑白棋棋子(x, y)
typedef pair<int, int> PII;
// 棋盘大小
const int rows = 8, cols = 8;
// 代表上、右上、右、右下、下、左下、左、左上八个方向的方向向量
int dx[] = {-1, -1, 0, 1, 1, 1, 0, -1}, dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
// 棋盘(值为0表示可下，1表示我方棋子，2表示敌方棋子)
int g[rows][cols] = {0};
// 分别代表我方和敌方棋子个数
int as, bs;
// 分别代表我方和敌方棋子
vector<PII> a, b;

// 由你来编写
// 返回PII类型的棋子坐标
PII next() {
    // 示例：
    // 策略：找出能消最多的对方棋子的位置下子，为了预防超时，当有两个位置可以下则找这两个位置中最长连线直接下子
    // 通过大根堆来存储我可以下的位置和对应的敌方棋子个数，键为敌方棋子个数，值为ps数组下标对应的棋子坐标
    priority_queue<PII, vector<PII>, less<PII> > heap;
    // 我方可下的位置
    vector<PII> ps;
    // 遍历棋盘
    for(int i = 0; i < rows; i++) {
        for(int j = 0; j < cols; j++) {
            // 若(i, j)是我方棋子
            if(g[i][j] == 1) {
                // 遍历8个方向
                for(int k = 0; k < 8; k++) {
                    int x = i + dx[k], y = j + dy[k];
                    int cnt = 0;
                    // 统计方向k有多少个连续的敌方棋子
                    while(x >= 0 && x < rows && y >= 0 && y < cols && g[x][y] == 2) {
                        cnt++;
                        x += dx[k], y += dy[k];
                    }
                    // 若方向k上另一边可下棋
                    if(x >= 0 && x < rows && y >= 0 && y < cols && g[x][y] == 0) {
                        // 将敌方棋个数作为键，我方可下位置作为ps下标
                        heap.push({cnt, ps.size()});
                        ps.push_back({x, y});
                    }
                    // 防止复杂度太高，出现本回合无操作情况，当有2个位置可以下子时，则直接操作
                    if(heap.size() >= 2) {
                        int t = heap.top().second;
                        return ps[t];
                    }
                }
            }
        }
    }
    // 若无地可下则不输出
    if(heap.empty()) {
    } else {
        // 返回敌方棋子个数最多的位置
        int t = heap.top().second;
        return ps[t];
    }
}

int main() {
    // 获取我方棋子的个数和位置
    cin >> as;
    for(int i = 0; i < as; i++) {
        int x, y;
        cin >> x >> y;
        a.push_back({x, y});
        // 标记棋盘
        g[x][y] = 1;
    }
    
    // 获取敌方棋子的个数和位置
    cin >> bs;
    for(int i = 0; i < bs; i++) {
        int x, y;
        cin >> x >> y;
        b.push_back({x, y});
        // 标记棋盘
        g[x][y] = 2;
    }
    
    PII t = next();
    cout << t.first << " " << t.second << endl;
    return 0;
}
```

其他游戏施工中希望不会🕊x







## 简要架构设计：

![img8.png](https://s2.loli.net/2024/07/24/UQja3PEBbK5G9mo.png)

## 一些其他的功能：

基于jwt的安全系统

基于微服务的匹配系统

基于docker的代码执行容器

## 鸣谢
