# OAHRService
鼓点·OAHR服务

[![](https://jitpack.io/v/githubAtom/OAHRService.svg)](https://jitpack.io/#githubAtom/OAHRService)


### 引入

implementation 'com.github.githubAtom:OAHRService:1.0.6'


### 调用HR界面

    /**
     * 打开HR界面
     *
     * @param context      上下文
     * @param hrToken      hr身份令牌
     * @param watermarkStr 水印文字
     */
    public static void openHR(Context context, String hrToken, String watermarkStr);

    /**
     * 打开HR界面
     *
     * @param context 上下文
     * @param hrToken hr身份令牌
     */
    public static void openHR(Context context, String hrToken);
    
    
    
