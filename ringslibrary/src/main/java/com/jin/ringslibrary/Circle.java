package com.jin.ringslibrary;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;

/**
 * Created by sg561 on 2018/3/25.
 */

public class Circle extends View {
    private Paint paint1;
    private Path path1;
    private Path path2;
    private Path path3;
    private int center2innercircle;
    private int textSize;
    private String ringpencent;
    private float ringwidth;
    private float ringspacing;
    public String[] percents;
    int belowcircleColor;
    int upsidecircleColor;
    String ringsColor;
    String[] ringsColors;
    List<Path> rings= new ArrayList<>();
    List<Paint> ringsPaint= new ArrayList<>();
    List<Paint> circlesPaint= new ArrayList<>();
    List<Paint> textPaint= new ArrayList<>();
    float[] textX;
    float[] textY;

    public Circle(Context context) {
        this(context, null);
    }

    public Circle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Circle(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        Init(attrs);
    }
    private void Init(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Rings);
        belowcircleColor = typedArray.getColor(R.styleable.Rings_belowcircleColor, Color.BLUE);
        upsidecircleColor = typedArray.getColor(R.styleable.Rings_upsidecircleColor, Color.BLACK);
        ringsColor = typedArray.getString(R.styleable.Rings_ringscolor);
        ringspacing=typedArray.getDimension(R.styleable.Rings_ringspacing, dp2px(20));
        ringwidth=typedArray.getDimension(R.styleable.Rings_ringwidth, dp2px(20));
        ringpencent=typedArray.getString(R.styleable.Rings_ringpercent);
        textSize=typedArray.getInteger(R.styleable.Rings_textsize,14);
        center2innercircle=(int) typedArray.getDimension(R.styleable.Rings_center2innercircle, dp2px(200));

        percents=ringpencent.split(",");

        ringsColors=ringsColor.split(",");

        textX=new float[percents.length];
        textY=new float[percents.length];

        paint1=new Paint();
        paint1.setColor(belowcircleColor);
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(1);
        paint1.setStyle(Paint.Style.STROKE);
        for (int i=0;i<ringsColors.length;i++)
        {
            Paint paint=new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor(ringsColors[i]));
            paint.setAlpha(150);
            ringsPaint.add(paint);
        }
        for (int i=0;i<ringsColors.length;i++)
        {
            Paint paint=new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(ringsColors[i]));
            paint.setStrokeWidth(3);
            circlesPaint.add(paint);
        }
        for(int i=0;i<percents.length;i++)
        {
            rings.add(new Path());
        }
        for(int i=0;i<percents.length;i++)
        {
            Paint paint=new Paint();
            String text=String.valueOf((Float.parseFloat(percents[i])*100))+"%";
            Rect bound=new Rect();
            paint.setColor(Color.parseColor(ringsColors[i]));
            paint.setTextSize(textSize);
            paint.getTextBounds(text,0,text.length(),bound);
            textPaint.add(paint);
        }

    }
    private int dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == heightMode && widthMode == MeasureSpec.AT_MOST) {
            int size = min(widthSize, heightSize);
            size = min(size, center2innercircle);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SetPath(percents);
        canvas.drawPath(path1, paint1);
        for(int i=0;i<rings.size();i++){
            canvas.drawPath(rings.get(i),ringsPaint.get(i));
            canvas.drawPath(rings.get(i),circlesPaint.get(i));
            canvas.drawText(String.valueOf((int) (Float.parseFloat(percents[i])*100))+"%",textX[i],textY[i],textPaint.get(i));
        }
    }
    private void SetPath(String[] percents){
        float width=getWidth();
        float height=getHeight();
        float innerradius;
        float outterradius;
        RectF innerringRectf;
        RectF outterringRectf;
        float center2innercircle_=center2innercircle;
        float ringwidth_=ringwidth;
        double x,y,m,n;
        path1=new Path();
        path2=new Path();
        for(int i=0;i<percents.length;i++)
        {
            textX[i]=getWidth()/2-ringwidth*1.3f;
            textY[i]=getHeight()/2-i*(ringspacing+ringwidth)-ringwidth*1.25f;
        }
        for(int i=0;i<percents.length;i++)
        {
            innerradius=center2innercircle_+ringwidth_+i*(ringspacing+ringwidth_);
            outterradius=center2innercircle_+i*(ringspacing+ringwidth_);
            innerringRectf=new RectF(width/2-innerradius,height/2-innerradius,width/2+innerradius,height/2+innerradius);
            outterringRectf=new RectF(width/2-outterradius,height/2-outterradius,width/2+outterradius,height/2+outterradius);


            path1.addCircle(width/2,height/2,outterradius, Path.Direction.CW);
            path1.addCircle(width/2,height/2,innerradius, Path.Direction.CW);


            path2.moveTo(width/2,height/2-outterradius);
            path2.quadTo(width/2-ringwidth_/2,height/2-outterradius+((height/2-innerradius)-(height/2-outterradius))/2,
                    width/2,height/2-innerradius);
            path2.arcTo(innerringRectf,-90,Float.parseFloat(percents[i])*360);


            x=((width/2+innerradius*cos((Float.parseFloat(percents[i])*360-90)*PI/180))+(width/2+outterradius*cos((Float.parseFloat(percents[i])*360-90)*PI/180)))/2;
            y=((height/2+innerradius*sin((Float.parseFloat(percents[i])*360-90)*PI/180))+(height/2+outterradius*sin((Float.parseFloat(percents[i])*360-90)*PI/180)))/2;

            double slpoe=-(y-height/2)/(x-width/2);

            double angle=atan(slpoe);
            if (Float.parseFloat(percents[i])>0&&Float.parseFloat(percents[i])<=0.5)
            {
                m=x+sin(angle)*ringwidth_/2;
                n=y+cos(angle)*ringwidth_/2;
            }
            else
            {

                m=x-sin(angle)*ringwidth_/2;
                n=y-cos(angle)*ringwidth_/2;
            }

            path2.quadTo((float)m,(float)n, (float)(width/2+outterradius*cos((Float.parseFloat(percents[i])*360-90)*PI/180)),(float) (height/2+outterradius*sin((Float.parseFloat(percents[i])*360-90)*PI/180)));
            path2.arcTo(outterringRectf,Float.parseFloat(percents[i])*360-90,-Float.parseFloat(percents[i])*360);
            path2.close();
            rings.set(i,path2);
            path2=null;
            path2=new Path();
        }
    }
    public void animation() {
        final Circle c = (Circle) this;
        String[] start=new String[c.percents.length];
        for(int i=0;i<start.length;i++){start[i]="0";}
        String[] end=c.percents;
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new mTypeEvaluator(),start,end);
        valueAnimator.setTarget(c);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                c.percents=(String[]) animator.getAnimatedValue();
                c.invalidate();
            }
        });
        valueAnimator.start();
    }
    public  class mTypeEvaluator implements TypeEvaluator<String[]> {
        @Override
        public String[] evaluate(float fraction,String[] startvalue,String[] endvalue){
            List<String> percents=new ArrayList<String>();
            for(int i=0;i<startvalue.length;i++)
            {
                percents.add(String.valueOf(Float.parseFloat(endvalue[i])*fraction));
            }
            String[] percents_ = new String[percents.size()];
            return percents.toArray(percents_);
        }
    }

}
