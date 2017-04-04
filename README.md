###一、简介
>在进行Android开发过程中我们知道Activity的重要性，但是自android3.0之后，android中出现的Fragment重要性其实不亚于Activity，今天简单的介绍一下Fragment之间进行数据传递的三种方式。

---
###二、准备工作
>step 1:创建一个Activity，命名为MainActivity，对应的布局文件如下(只截取核心部分)：
```
<FrameLayout
    android:id="@+id/fl_menu"
    android:layout_width="0dp"
    android:layout_weight="1"
    android:background="#20ff0000"
    android:layout_height="match_parent"/>
<FrameLayout
    android:id="@+id/fl_main"
    android:layout_width="0dp"
    android:layout_weight="2"
    android:background="#2500ff00"
    android:layout_height="match_parent"/>
```
step2:创建两个Fragment分别为MenuFragment和MainFragment,对应的布局如下：
MenuFragment的核心布局：
```
<ListView
    android:id="@+id/lv_menu"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
MainFragment核心布局：
```
<Button
    android:id="@+id/bt_main"
    android:background="#ff0"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:text="根据条目改变内容"
    android:layout_centerInParent="true"/>
```
step3:两个Fragment里面各自将布局引进（onCreateView()方法中）（具体代码请参考源码），需要注意的是MenuFragment里面是一个ListView,这里手动添加了5个数据填充进去；
step4:在MainActivity的onCreate()方法中引入这两个Fragment:
```
MenuFragment menuFragment = new MenuFragment();
MainFragment mainFragment  = new MainFragment();
//将上面的两个Fragment添加进来
getSupportFragmentManager().beginTransaction().replace(R.id.fl_menu, menuFragment, "menuFragment").commit();
getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, mainFragment, "mainFragment").commit();
```

需要展示的界面如下：

![界面展示](http://upload-images.jianshu.io/upload_images/3009951-19b17ee9bc462724.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
以上准备工作完毕！

---
###三、三种数据传递方式的相关操作
>**需求**：我们点击上面图片中展示的对应的条目数据，让右侧（黄色按钮）对应的文本数据更换成条目展示的文本，例如：我点击左侧第4个条目，右侧按钮文本立马更改成“这是第4条数据”，从而实现Fragment之间数据的传递。下面开始介绍三种方法实现Fragment之间数据的传递。

---
>**方法一**：
* 1、在MainFragment中设置一个setData()方法，在方法中设置更改按钮名称;
```
//MainFragment.java文件中
public void setData(String string) {
    bt_main.setText(string);
}
```
* 2、在MenuFragment中的ListView条目点击事件中通过标签获取到MainFragment，并调用对应的setData()方法，将数据设置进去，从而达到数据传递的目的。
```
lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          MainFragment mainFragment =
               (MainFragment) getActivity()
               .getSupportFragmentManager()
               .findFragmentByTag("mainFragment");
          mainFragment.setData(mDatas.get(position));
     }
});
``` 
只需上面区区两步即可达到数据传递的目的。

---
>**方法二**：
采取接口回调的方式进行数据传递。
* step1: 在Menuragment中创建一个接口以及接口对应的set方法：
```
//MenuFragment.java文件中
public interface OnDataTransmissionListener {
    public void dataTransmission(String data);
}
public void setOnDataTransmissionListener(OnDataTransmissionListener mListener) {
    this.mListener = mListener;
}
```
* step2: 在MenuFragment中的ListView条目点击事件中进行接口进行接口回调
```
//MenuFragment.java文件中
lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { 
   @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         * 方法二：采取接口回调的方式进行数据传递
         */
        if (mListener != null) {
            mListener.dataTransmission(mDatas.get(position));
        }
    }
});
```
* step3: 在MainActivity中根据menuFragment获取到接口的set方法，在这个方法中进行进行数据传递，具体如下：
```
//在MainActivity.java中
menuFragment.setOnDataTransmissionListener(new MenuFragment.OnDataTransmissionListener() {
    @Override
    public void dataTransmission(String data) {
        mainFragment.setData(data);  //注：对应的mainFragment此时应该要用final进行修饰
    }
});
```
通过上面的三步也可以轻松做到Fragment数据之间的传递。

---
>**方法三**：
使用三方开源框架：**EventBus**
那么问题来了：EventBus是个啥东西？？？
简单来说，**EventBus**是一款针对Android优化的发布/订阅（publish/subscribe）事件总线。主要功能是替代Intent,Handler,BroadCast在Fragment，Activity，Service，线程之间传递消息。简化了应用程序内各组件间、组件与后台线程间的通信。**优点是开销小，代码更优雅，以及将发送者和接收者解耦。**比如请求网络，等网络返回时通过Handler或Broadcast通知UI，两个Fragment之间需要通过Listener通信，这些需求都可以通过**EventBus**实现。
下面我们就用EventBus来实现以下Fragment之间的数据传递：
* step1：引入EventBus
```
compile 'org.greenrobot:eventbus:3.0.0'
```
* step2：注册事件接收者
这里MainFragment是要接收MenuFragment发送来的数据，所以我们在MainFragment中的onCreateView()方法中进行注册：
```
EventBus.getDefault().register(this);
```
* step3：发送事件
**注：**发送事件之前其实还有一步定义事件类型，这里我们传递的数据只有一个类型，所以这一步取消了。
MenuFragment发送数据给MainFragment，所以我们在MenuFragment中将要传递的数据进行发送事件操作：
```
lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventBus.getDefault().post(mDatas.get(position));
    }
});
```
* step4：接收消息并处理
在MainFragment中我们接收来自MenuFragment传递过来的数据,并进行对应的处理（注：EventBus 3.0版本这一步必须要写注解@Subscribe (与2.4版本有所区别)）：
```
@Subscribe
public void onEvent(String data) {
    bt_main.setText(data);
}
```
通过上面这一步即可完成数据之间的传递，需要注意的是在销毁的时候我们要注销事件接收。
* step5：注销事件接收
```
//MainFragment.java中
@Override
public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
}
```
以上五步完成了Fragment之间的数据传递，看似比上面两个方法要复杂的多，但当我们涉及到复杂的Fragment之间数据传递（例如Fragment中嵌套多层Fragment）时，就会体会到EventBus的爽快之处~~~这里不进行赘述了。

---
###四、总结
>对于以上三种方法，各自有各自的优点，我们可以根据实际需求采用对应的方法；对于EventBus使用有不明白的地方，这里推荐一片博客供大家学习参考：http://gold.xitu.io/entry/570ae5668ac247004c3128a4

以上三个方法的讲解源码链接如下：
【源码连接】https://github.com/Reign9201/datatransmission
