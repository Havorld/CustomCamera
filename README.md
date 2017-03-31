# CustomCamera
## 博客地址：[http://blog.csdn.net/xiaohao0724/article/details/68488145](http://blog.csdn.net/xiaohao0724/article/details/68488145)
这两天在研究自定义Camera,解决了预览变形、横屏问题下面来记录下成果。
老规矩先上图:


# 要想自定义相机需要用到两个核心类：Camera和SurfaceView
## Camera类一些API
Camera用于管理和操作camera资源，它提供了完整的相机底层接口。可以通过camera.getParameters()来获取Camera的参数用以设置照片的预览尺寸、拍摄尺寸、预览帧、拍摄帧、设定光圈、曝光、聚焦、颜色特效等。


## SurfaceView类
用于绘制相机预览图像的类，提供给用户实时的预览图像。普通的view以及派生类都是共享同一个surface的，所有的绘制都必须在UI线程中进行。而surfaceview是一种比较特殊的view，它并不与其他普通view共享surface，而是在内部持有了一个独立的surface,surfaceview负责管理这个surface的格式、尺寸以及显示位置。由于UI线程还要同时处理其他交互逻辑，因此对view的更新速度和帧率无法保证，而surfaceview由于持有一个独立的surface，因而可以在独立的线程中进行绘制，因此可以提供更高的帧率。自定义相机的预览图像由于对更新速度和帧率要求比较高，所以比较适合用surfaceview来显示。

### SurfaceHolder 
surfaceholder是控制surface的一个抽象接口，它能够控制surface的尺寸和格式，修改surface的像素，监视surface的变化等等，surfaceholder的典型应用就是用于surfaceview中。surfaceview通过getHolder()方法获得surfaceholder 实例，通过后者管理监听surface 的状态。

### SurfaceHolder.Callback 接口
负责监听surface状态变化的接口，有三个方法：
#### surfaceCreated(SurfaceHolder holder)
在surface创建后立即被调用。在开发自定义相机时，可以通过重载这个函数调用camera.open()、camera.setPreviewDisplay()，来实现获取相机资源、连接camera和surface等操作。
#### surfaceChanged(SurfaceHolder holder, int format, int width, int height)
在surface发生format或size变化时调用。在开发自定义相机时，可以通过重载这个函数调用camera.startPreview来开启相机预览，使得camera预览帧数据可以传递给surface，从而实时显示相机预览图像。
#### surfaceDestroyed(SurfaceHolder holder)
在surface销毁之前被调用。在开发自定义相机时，可以通过重载这个函数调用camera.stopPreview()，camera.release()来实现停止相机预览及释放相机资源等操作。
