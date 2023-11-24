# 大连理工大学1024程序员节Bot对战平台DUT BOTS

基于springboot+vue实现基于微服务的bot对战平台

DUT BOTS上的游戏属于回合制，需要用户通过C++/JAVA/PYTHON根据我们给出的模板编写Bot，BOT进行每一回合的操作，当云端接收到对局中两名用户的操作时，返回对局信息， 如果操作没有使得对局结束， 就会循环往复的进行读取BOT操作， 直到对局结束。

### 网站预览：



#### 首页：

![](https://s3.bmp.ovh/imgs/2023/11/24/579dd26d23f87b96.png)

#### 代码模板示例：

![](https://s3.bmp.ovh/imgs/2023/11/24/94542bae8ef1f594.png)

#### 天梯排行榜（学号等敏感信息已打🐎）:

![](https://s3.bmp.ovh/imgs/2023/11/24/bd444c0f3ecf2631.png)

**游戏内容：五子棋**

游戏规则
这是一个简单的五子棋游戏，属于回合制游戏。该游戏为双人对战模式，每一回合需要选择一个位置下棋。

![](https://s3.bmp.ovh/imgs/2023/11/24/c61b8aed3432f8fe.png)

**游戏内容：黑白棋**

游戏规则
[百度解释](https://baike.baidu.com/item/黑白棋/80689), 这是一个有趣简单的游戏，但是极其难精通，它属于回合制游戏。该游戏为双人对战模式，每一回合需要选择一个位置，此位置在横、竖、斜八个方向内有一个自己的棋子，则被夹在中间的全部翻转会成为自己的棋子。并且，只有在可以翻转棋子的地方才可以下子。



![](https://s3.bmp.ovh/imgs/2023/11/24/5cc77ed417df3670.png)



其他游戏施工中希望不会🕊x







#### 简要架构设计：

![](https://s3.bmp.ovh/imgs/2023/11/24/254871e1a286d093.png)

#### 一些其他的功能：

基于jwt的安全系统

基于微服务的匹配系统

基于docker的代码执行容器

### 鸣谢