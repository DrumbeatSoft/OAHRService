# OAHRService
鼓点·OAHR服务

[![](https://jitpack.io/v/githubAtom/OAHRService.svg)](https://jitpack.io/#githubAtom/OAHRService)


### 引入

implementation 'com.github.DrumbeatSoft:OAHRService:1.1.12'


### 调用HR界面

                   //配置HRService
                   HRService hrService = HRService.from(MainActivity.this)
                        //设置服务器地址
                        .setBaseUrl(BASE_URL, BASE_URL_H5)
                        //设置身份令牌
                        .setHrToken(token)
                        //设置水印文字 非必要参数
                        .setWatermarkStr("");
                        
                   //开启HR界面
                   hrService.startHR();
                   
                   //获取代办事项数量
                   hrService.getTodoCount(new HRService.TodoCountListener() {
                        @Override
                        public void getTodoCount(int todoCount) {

                        }

                        @Override
                        public void onFailed(String failed) {

                        }
                    });
    

    
