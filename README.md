# OAHRService
鼓点·OAHR服务

[![](https://jitpack.io/v/githubAtom/OAHRService.svg)](https://jitpack.io/#githubAtom/OAHRService)


### 引入

implementation 'com.github.DrumbeatSoft:OAHRService:1.1.7'


### 调用HR界面

                   HRService.from(MainActivity.this)
                        //设置服务器地址
                        .setBaseUrl(BASE_URL, BASE_URL_H5)
                        //设置身份令牌
                        .setHrToken(token)
                        //设置水印文字 非必要参数
                        .setWatermarkStr("")
                        .startHR();

    

    
