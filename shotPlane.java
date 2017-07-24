package com.example.harry.sheldon;

/**
 * Created by Hermione on 2016/2/15.
 */

        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.res.Resources;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Rect;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.SoundPool;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.View;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;
        import java.util.TreeSet;
        import java.util.logging.LoggingPermission;

/**
 * Created by Hermione on 2016/2/13.
 */
public class shotPlane extends SurfaceView implements Runnable,SurfaceHolder.Callback,android.view.View.OnTouchListener{
    private Bitmap myPlane;
    private Bitmap background;
    private Bitmap bullet;
    private Bitmap enemys;
    private Bitmap bug;
    private Bitmap explosion;
    private int Display_w,Display_h;
    private ArrayList<gamePicture> gameImages=new ArrayList<gamePicture>();
    private ArrayList<gamePicture> bulletBarn=new ArrayList<gamePicture>();
    private ArrayList<fly_text> fly_texts=new ArrayList<fly_text>();
    private Bitmap buffers=null;
    private boolean state=false,Ispause=false,Isgameover=false;
    private SurfaceHolder holder;
    private Random ran=new Random();
    private int score=0,countbug=0,eposide=0;
    private boolean Gobug=false;
    private Thread mythread;
    private planeImage selectedplan;
    private SoundPool pool=null;
    private int sound_bomb=0;
    private int sound_bullet=0;
    private int sound_gameOver=0;
    private MediaPlayer mp3_bgm=null;
    private planeImage mypilot=null;
    private boolean Isrestart=false;
    private boolean Iswin=false;
    private boolean temp_key=false;

    private final int[][] hard_parameter={
            {1,200,4,1,40},//关卡序号，过关分数，敌机速度，，敌机血量，出现敌机周期；
            {2,200,8,2,35},
            {3,300,10,2,30},
            {4,400,12,3,25},
            {5,500,14,3,20},
            {6,600,16,3,15},
            {7,700,16,3,13},
            {8,800,16,3,10},


    };
    private int time_counter=0;
    private int Ts=20;//这是游戏的帧率；
    private boolean show_eposide=false;
    private int bullet_vt=3;//子弹数量生成速度，帧率50时取2；
    private int text_vt=2;

    public shotPlane(Context context) {
        super(context);
        getHolder().addCallback(this);
        this.setOnTouchListener(this);//事件注册；
        Log.i("app.tager","事件注册！------");

    }
    private void init(){

        myPlane= BitmapFactory.decodeResource(getResources(), R.drawable.ally);
        background= BitmapFactory.decodeResource(getResources(),R.drawable.pure);
        bullet= BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
        enemys= BitmapFactory.decodeResource(getResources(), R.drawable.enemys);
        explosion= BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
        bug=BitmapFactory.decodeResource(getResources(), R.drawable.bug);

        gameImages.add(new Background(background));

        mypilot=new planeImage(myPlane);
        gameImages.add(mypilot);
        RenewEnemy();

        //生成二级缓存照片；
        buffers=Bitmap.createBitmap(Display_w, Display_h, Bitmap.Config.ARGB_8888);

        //生成音频池；
        pool=new SoundPool(15, AudioManager.STREAM_SYSTEM,0);

        sound_bomb=pool.load(getContext(),R.raw.bomb,1);
        sound_gameOver=pool.load(getContext(),R.raw.gameover,1);
        sound_bullet=pool.load(getContext(), R.raw.m4a1_soundless, 1);

        mp3_bgm=MediaPlayer.create(getContext(), R.raw.start_war);
        mp3_bgm.setLooping(true);
        mp3_bgm.start();







    }

    private void RenewEnemy() {
        gameImages.add(new enemyPlane(enemys,hard_parameter[eposide][2],hard_parameter[eposide][3]));
    }

    //重新开始，设置复原；
    public void restart(){

                show_eposide=false;
                time_counter=0;
                selectedplan=null;
                Gobug=false;
                score=0;
                countbug=0;
                eposide=0;
                state=false;
                Ispause=false;
                Isgameover=false;
                fly_texts.clear();
                gameImages.clear();
                bulletBarn.clear();
                pool.release();
                mp3_bgm.release();
                init();
                state=true;
                Isrestart=false;
                Iswin=false;
                temp_key=false;



    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            Log.i("app.tager","检测到点击！------");
            for (gamePicture game: gameImages){
                if(game instanceof planeImage){
                    planeImage feiji=(planeImage)game;
                    if(feiji.getx()<event.getX()
                            &&feiji.gety()<event.getY()
                            &&feiji.getWidth()+feiji.getx()>event.getX()
                            &&feiji.getHeight()+feiji.gety()>event.getY()){
                        selectedplan=feiji;
                    }else {
                        selectedplan=null;
                    }
                    break;
                }
            }
            if(0<event.getX()
                    &&0<event.getY()
                    &&100>event.getX()
                    &100>event.getY()){
                countbug++;
                if(countbug>3){
                    Gobug=true;
                }
            }

        }else if(event.getAction()==MotionEvent.ACTION_MOVE){

            if(selectedplan!=null){
                selectedplan.setX((int)event.getX()-selectedplan.getWidth()/2);
                selectedplan.setY((int) event.getY() - selectedplan.getHeight() / 2);


            }
        }else if(event.getAction()==MotionEvent.ACTION_UP){

            selectedplan=null;
        }
        return true;
    }

    //我们自己建立的接口；
    private interface gamePicture{
        public Bitmap getBitmap();
        public int getx();
        public int gety();

    }

    private class thread extends Thread{
        private int soundIndex=0;
        public thread(int soundIndex){
            this.soundIndex=soundIndex;
        }

        @Override
        public void run() {

            pool.play(soundIndex,(float)0.5,(float)0.5,1,0,1);

        }
    }

    //创建弹幕；
    private class fly_text {
        private String text=null;
        private int x=0,y=0;
        private Paint p3=null;

        public fly_text(String text, int text_size,int text_shelft) {
            this.text = text;
            this.x=text_shelft;
            this.y=Display_h-200;
            p3=new Paint();
            p3.setColor(Color.YELLOW);
            p3.setTextSize(text_size);
            p3.setDither(true);
            p3.setAntiAlias(true);//抗锯齿

        }
        public void drawtexts(Canvas newcanvas){
            y-=text_vt;
            newcanvas.drawText(text,x, y, p3);
            if(y<-100){
                fly_texts.remove(this);
            }
        }


    }
    //创建子弹图片；
    private class bulletImage implements gamePicture{

        private Bitmap bullet;
        private int x,y;

        public bulletImage(Bitmap bullet,int x,int y) {
            this.bullet = bullet;
            this.x=x-bullet.getWidth()/2;
            this.y=y;
        }

        @Override
        public Bitmap getBitmap() {
            return bullet;
        }

        @Override
        public int getx() {
            return x;
        }

        @Override
        public int gety() {
            if(y<-bullet.getHeight()){
                bulletBarn.remove(this);

            }
            y-=45;
            return y;
        }
    }

    //创建敌机，获得敌机的图片；
    private class enemyPlane implements gamePicture{
        private int x=0,y=0;
        private List<Bitmap> bitmaps= new ArrayList<>();
        private List<Bitmap> explosions= new ArrayList<>();
        private Bitmap enemys=null;
        private int index=0,endExplosion=0;
        private int Vt;
        private boolean IsGetShot=false;//判断敌机是否马上清除；
        private boolean Isexplosion=false;//判断是否进入爆炸动画
        private int width=0,height=0;
        private int bloods_left=0;

        public boolean isGetShot() {
            return IsGetShot;
        }

        public enemyPlane(Bitmap enemys,int Vt,int bloods_left) {
            this.enemys=enemys;
            x=ran.nextInt(Display_w-enemys.getWidth()/4);//这里的4应当随着动画图片的个数而变化；
            this.y=-enemys.getHeight();
//            Vt=ran.nextInt(20)+5;
            this.Vt=Vt;
            this.bloods_left=bloods_left;
            width=enemys.getWidth()/4;
            height=enemys.getHeight();

            bitmaps.add(Bitmap.createBitmap(enemys,0,0,enemys.getWidth()/4,enemys.getHeight()));
            bitmaps.add(Bitmap.createBitmap(enemys,enemys.getWidth()/4,0,enemys.getWidth()/4,enemys.getHeight()));
            bitmaps.add(Bitmap.createBitmap(enemys,enemys.getWidth()*2/4,0,enemys.getWidth()/4,enemys.getHeight()));
            bitmaps.add(Bitmap.createBitmap(enemys, enemys.getWidth() * 3 / 4, 0, enemys.getWidth() / 4, enemys.getHeight()));

            explosions.add(Bitmap.createBitmap(explosion,0,0,explosion.getWidth()/4,explosion.getHeight()/2));
            explosions.add(Bitmap.createBitmap(explosion,explosion.getWidth()/4,0,explosion.getWidth()/4,explosion.getHeight()/2));
            explosions.add(Bitmap.createBitmap(explosion,explosion.getWidth()*2/4,0,explosion.getWidth()/4,explosion.getHeight()/2));
            explosions.add(Bitmap.createBitmap(explosion, explosion.getWidth() * 3 / 4, 0, explosion.getWidth() / 4, explosion.getHeight()/2));
            explosions.add(Bitmap.createBitmap(explosion,0,explosion.getHeight()/2,explosion.getWidth()/4,explosion.getHeight()/2));
            explosions.add(Bitmap.createBitmap(explosion,explosion.getWidth()/4,explosion.getHeight()/2,explosion.getWidth()/4,explosion.getHeight()/2));
            explosions.add(Bitmap.createBitmap(explosion,explosion.getWidth()*2/4,explosion.getHeight()/2,explosion.getWidth()/4,explosion.getHeight()/2));
            explosions.add(Bitmap.createBitmap(explosion, explosion.getWidth() * 3 / 4, explosion.getHeight()/2, explosion.getWidth() / 4, explosion.getHeight()/2));

        }


        @Override
        public Bitmap getBitmap() {
            if(Isexplosion){
                if(endExplosion>=explosions.size()-1){

                    IsGetShot=true;


                }
                return explosions.get(endExplosion++);
            }else {
                if(index>=bitmaps.size()*10){
                    index=0;
                }
                if(!IsGetShot){
                    getShoot();
                    getCrash(mypilot);
                }
                return bitmaps.get(index++/10);
            }

        }

        public void getShoot(){
            for(gamePicture g:(List<gamePicture>)bulletBarn.clone()){
                if(g.getx()>x&&g.getx()<x+width&&g.gety()<y+height&&g.gety()>y-13){//这里也涉及是否有敌机动画图片；假设子弹长为13；

                    bloods_left--;
                    if(bloods_left<1){
                        Isexplosion=true;//将进入爆炸动画，并最后结束这个对象；
                    }
                    bulletBarn.remove(g);
                    score+=10;
                    new thread(sound_bomb).start();
                    break;
                }
            }
        }

        public void getCrash(planeImage mypilot){
            if(mypilot.getx()+mypilot.getWidth()/2>x-mypilot.getWidth()/3
                    &&mypilot.getx()+mypilot.getWidth()/2<x+width+mypilot.getWidth()/3
                    &&mypilot.gety()<y+height
                    &&mypilot.gety()>y-mypilot.getHeight()){
                Isexplosion=true;
                Isgameover=true;
                new thread(sound_gameOver).start();
            }
        }

        @Override
        public int getx() {
            return x;
        }

        @Override
        public int gety() {
            if(y>=Display_h){
                Isgameover=true;
                gameImages.remove(this);
            }
            y+=Vt;
            return y;
        }


    }

    //创建飞机，获得飞机的图片；
    private class planeImage implements gamePicture{

        private Bitmap myPlane;
        private List<Bitmap> bitmaps=new ArrayList<Bitmap>();
        private int index=0;
        private int x,y;
        private int width,height;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public int getx() {
            return x;
        }

        @Override
        public int gety() {
            return y;
        }
        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public planeImage(Bitmap myPlane) {
            this.myPlane = myPlane;
            x=(Display_w-myPlane.getWidth())/2;//如果你的战机有动画，则这里需要用截取后的战机尺寸；
            y=Display_h-myPlane.getHeight()-20;
            width=myPlane.getWidth();//如果你的战机有动画，则这里需要用截取后的战机尺寸；
            height=myPlane.getHeight();//如果你的战机有动画，则这里需要用截取后的战机尺寸；

            bitmaps.add(myPlane);
//下面是有战机动画时的截取动画照片的过程；
//            bitmaps.add(Bitmap.createBitmap(myPlane,0,0,myPlane.getWidth()/4,myPlane.getHeight()));
//            bitmaps.add(Bitmap.createBitmap(myPlane, myPlane.getWidth() / 4, 0, myPlane.getWidth() / 4, myPlane.getHeight()));
//            bitmaps.add(Bitmap.createBitmap(myPlane,myPlane.getWidth() * 2 / 4,0,myPlane.getWidth()/4,myPlane.getHeight()));
//            bitmaps.add(Bitmap.createBitmap(myPlane, myPlane.getWidth() * 3 / 4, 0, myPlane.getWidth()/4, myPlane.getHeight()));
        }
        @Override
        public Bitmap getBitmap() {
            if (index>=bitmaps.size()*10){//这个10和下面的10用于降低飞机更新的速度；
                index=0;
            }
            return bitmaps.get(index++/10);//这个10和上面的10用于降低飞机更新的速度；
        }


    }

    //创建背景，获得背景图片；
    private class Background implements gamePicture{
        private Bitmap bg;
        private Bitmap newBitmap=null;
        private int height=0;
        public Background(Bitmap bg) {
            this.bg = bg;
            newBitmap=Bitmap.createBitmap(Display_w,Display_h, Bitmap.Config.ARGB_8888);

        }
        @Override
        public int getx() {
            return 0;
        }

        @Override
        public int gety() {
            return 0;
        }

        public Bitmap getBitmap(){
            Paint p=new Paint();
            Canvas canvas=new Canvas(newBitmap);
            canvas.drawBitmap(bg,new Rect(0,0,bg.getWidth(),bg.getHeight()),
                    new Rect(0,height,Display_w,Display_h+height),p);
            canvas.drawBitmap(bg,new Rect(0,0,bg.getWidth(),bg.getHeight()),
                    new Rect(0,-Display_h+height,Display_w,height),p);
            height+=2;
            if(height>=Display_h){
                height=0;
            }

            return newBitmap;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        Log.i("aaa.tager","surfaceCreated");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//得到屏幕尺寸；
        Log.i("aaa.tager","surfaceChanged");
        Display_w=width;
        Display_h=height;
        init();
        this.holder=holder;
        state=true;
        mythread=new Thread(this);
        mythread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        state=false;
    }

    public void stop(){
        Ispause=true;
    }
    public void start(){
        Ispause=false;
        mythread.interrupt();//叫线程起来，中止睡眠；
    }
    @Override
    public void run() {
        Paint p1=new Paint();

        //设置画笔
        Paint p2=new Paint();
        p2.setColor(Color.YELLOW);
        p2.setTextSize(50);
        p2.setDither(true);
        p2.setAntiAlias(true);//抗锯齿


        int counter=0,bullet_num=0,time_counter=0;
        try{

            while ((state)){

                if(Isrestart){
                    time_counter++;
                    if(time_counter*Ts/1000>2){
                        Isrestart=false;
                        time_counter=0;
                        Iswin=false;
                        restart();
                    }

                }
                while(Ispause){
                    try {

                        Thread.sleep(1000000);
                    }catch (Exception e){
                        Log.i("app.tager","sleep error"+hard_parameter.length,e);
                    }
                }
                Canvas newcanvas = new Canvas(buffers);

                if(counter>=hard_parameter[eposide][4]){
                    RenewEnemy();
                    counter=0;
                    Log.i("app.tager", bulletBarn.size() + ",  " + gameImages.size());
                }
                counter++;

                //这里画子弹；
                if(selectedplan!=null&&bullet_num>=bullet_vt){//在线程里创建子弹比在事件捕捉器里创建更好，因为事件变化太快，有时比设定的线程循环周期还快，导致空指针的错误；
                    bulletBarn.add(new bulletImage(bullet,selectedplan.getx()+selectedplan.getWidth()/2,selectedplan.gety()));
                    bullet_num=0;
                    new thread(sound_bullet).start();
                }
                bullet_num++;

                //这里画飞机；
                for(gamePicture g:(List<gamePicture>)gameImages.clone()){//这里有个克隆，因为其他地方有代码会删除gameImages的元素，
                    // 这里不想掺和；这个很有必要，可以避免出现ConcurrentModificationException；

                    newcanvas.drawBitmap(g.getBitmap(), g.getx(), g.gety(), p1);
                    if(g instanceof  enemyPlane){
                        if(((enemyPlane) g).isGetShot()){

                            gameImages.remove(g);

                        }
                    }
                    if(Isgameover&&!Isrestart){//Isrestat可以保证条件语句只做一次；
                        gameImages.remove(mypilot);
//                        bulletBarn.clear();
                        fly_texts.clear();
                        Isrestart=true;
                        fly_texts.add(new fly_text("Restart in Two seconds.",50,Display_w/2-200));
                        Isgameover=false;
                       for(gamePicture g_enemy:(List<gamePicture>)gameImages.clone()){
                           if(g_enemy instanceof enemyPlane){
                               gameImages.remove(g_enemy);
                           }
                       }
                        mypilot.setX(-300);
                        mypilot.setY(-300);

                        break;

                    }

                    //这里借前面定义的窗口期显示文字；
                    if(Isrestart&&!Iswin){
                        newcanvas.drawText("Defeat! ", Display_w / 2 - 50, Display_h / 2 - 50, p2);

                    }
                }

                //这里画子弹；
                for(gamePicture g:(List<gamePicture>)bulletBarn.clone()){
                    newcanvas.drawBitmap(g.getBitmap(),g.getx(),g.gety(),p1);
                }

                //下面是bug代码；
                if(Gobug){
                    newcanvas.drawBitmap(bug,new Rect(0,0,bug.getWidth(),bug.getHeight())
                            ,new Rect(0,0,Display_w,Display_h),p1);
                }

                //显示左上角积分板；
                newcanvas.drawText("分数： " + score, 0, 50, p2);
                newcanvas.drawText("关卡： " + hard_parameter[eposide][0], 0, 100, p2);
                newcanvas.drawText("本关分数： " + hard_parameter[eposide][1], 0, 150, p2);

                //判断是否该进入下一关；
                if(eposide<hard_parameter.length&&!Iswin){
                    if(score>=hard_parameter[eposide][1]){//只有当分数大于=本关分数时，才进入；
                        eposide++;
                        int temp=0;
                        if(eposide>=hard_parameter.length){//最后一关刚满分时
                            Iswin=true;
                            eposide=hard_parameter.length-1;
                            temp=score;
                        }
                        score=temp;

                        //显示第几关了；也只判决一次；
                        if(!Iswin&&!Isgameover) {
                            fly_texts.clear();
                            fly_texts.add(new fly_text("第 " + hard_parameter[eposide][0] + "关！", 50, Display_w / 2 - 50));
                            Log.i("app.tager", "epsoide:" + eposide + ",  flytextnum:" + fly_texts.size() + ",Iswin: " + Iswin + ", score:" + score);
                        }
                    }
                }
                if(Iswin) {
                    newcanvas.drawText("Congratulations! ", Display_w / 2 - 50, Display_h / 2 -50, p2);
                }


                for(fly_text g:(List<fly_text>)fly_texts.clone()){
                    g.drawtexts(newcanvas);
                }




                Canvas canvas=holder.lockCanvas();
                canvas.drawBitmap(buffers, 0, 0, p1);
                holder.unlockCanvasAndPost(canvas);
                Thread.sleep(Ts);

            }

        }catch (Exception e){
            Log.i("aaa.tager","yichang",e);
        };

    }
}

