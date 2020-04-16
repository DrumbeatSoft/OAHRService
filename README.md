# OAHRService
鼓点·OAHR服务

[![](https://jitpack.io/v/githubAtom/OAHRService.svg)](https://jitpack.io/#githubAtom/OAHRService)


### 引入

implementation 'com.github.githubAtom:OAHRService:1.1.2'


### 调用HR界面

     HRService.from(MainActivity.this)
                        //设置身份令牌
                        .setHrToken(token)
                        //设置水印文字 非必要参数
                        .setWatermarkStr("")
                        //切换为测试环境 配合调试 默认为不开启
                        .setTestService(true)
                        .startHR();
    

    
