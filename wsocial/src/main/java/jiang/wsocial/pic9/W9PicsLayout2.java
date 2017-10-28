package jiang.wsocial.pic9;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import jiang.wsocial.R;

/**
 * 仿微信 九宫格 图片布局
 */
public class W9PicsLayout2 extends FrameLayout implements View.OnClickListener {

    public static final int MAX_DISPLAY_COUNT = 9;
    private final LayoutParams lpChildImage = new LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT);
    private int mSingleMaxSize;
    private int mSpace;
    private final List<JDimImageView> iPictureList = new ArrayList<>();
    private final List<ImageView> mVisiblePictureList = new ArrayList<>();
    private final TextView tOverflowCount;

    private Callback mCallback;
    private boolean isInit;
    private List<String> mDataList;
    private List<String> mThumbDataList;
    private int mSingleMaxHSize;
    private int mAddMaskHolder;
    private int mPlaceHolder = R.drawable.default_picture;
    private int mErrorHolder = R.drawable.default_picture;

    public W9PicsLayout2(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        mSingleMaxSize = (int)( TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, mDisplayMetrics)+0.5f );
        mSpace = (int)( TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics)+0.5f );

        for(int i = 0; i<MAX_DISPLAY_COUNT; i++) {
            JDimImageView squareImageView = new JDimImageView(context);
            squareImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            squareImageView.setVisibility(View.GONE);
            squareImageView.setOnClickListener(this);
            addView(squareImageView);
            iPictureList.add(squareImageView);
        }

        tOverflowCount = new TextView(context);
        tOverflowCount.setTextColor(0xFFFFFFFF);
        tOverflowCount.setTextSize(24);
        tOverflowCount.setGravity(Gravity.CENTER);
        tOverflowCount.setBackgroundColor(0x66000000);
        tOverflowCount.setVisibility(View.GONE);
        addView(tOverflowCount);
    }

    public void set(List<String> urlThumbList, List<String> urlList){
        mThumbDataList = urlThumbList;
        mDataList = urlList;
        if(effectiveList(urlList) && effectiveList(urlThumbList) && mThumbDataList.size() == mDataList.size()) {
            if(isInit) {
                notifyDataChanged();
            }
        }else {
            setVisibility(GONE);
        }
    }


    public void set(List<String> urlList){
        mThumbDataList = mDataList = urlList;
        if(effectiveList(urlList)) {
            if(isInit) {
                notifyDataChanged();
            }
        }else {
            setVisibility(GONE);
        }
    }

    public boolean effectiveList(List checkList){
        return checkList != null && checkList.size()>0;
    }

    @SuppressLint("DefaultLocale")
    private void notifyDataChanged(){
        //数据是安全的
        setVisibility(View.VISIBLE);
        final List<String> thumbList = mThumbDataList;
        final int urlListSize = mThumbDataList.size();

        if(mThumbDataList.size()>mDataList.size()) {
            throw new IllegalArgumentException(
                    "dataList.size("+mDataList.size()+") > thumbDataList.size("+thumbList.size()+")");
        }
        //列
        int column = 3;
        if(urlListSize == 1) {
            column = 1;
        }else if(urlListSize == 4 || urlListSize == 2) {
            column = 2;
        }
        //行
        int row = 0;
        if(urlListSize>6) {
            row = 3;
        }else if(urlListSize>3) {
            row = 2;
        }else if(urlListSize>0) {
            row = 1;
        }

        final int imageSize = urlListSize == 1 ? mSingleMaxSize : (int)( ( getWidth()*1f-mSpace*( column-1 ) )/column );

        lpChildImage.width = imageSize;
        lpChildImage.height = urlListSize != 1 ? lpChildImage.width : mSingleMaxHSize != 0 ? mSingleMaxHSize : lpChildImage.width;

        tOverflowCount.setVisibility(urlListSize>MAX_DISPLAY_COUNT ? View.VISIBLE : View.GONE);
        tOverflowCount.setText(String.format("+ %d", urlListSize-MAX_DISPLAY_COUNT));
        tOverflowCount.setLayoutParams(lpChildImage);
        mVisiblePictureList.clear();
        for(int i = 0; i<iPictureList.size(); i++) {
            final JDimImageView iPicture = iPictureList.get(i);
            iPicture.setTag(null);
            if(i<urlListSize) {
                iPicture.setVisibility(View.VISIBLE);
                mVisiblePictureList.add(iPicture);
                iPicture.setLayoutParams(lpChildImage);
                //                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                //                        .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                Glide.with(getContext()).load(thumbList.get(i)).thumbnail(0.2f)
                        .apply(RequestOptions.placeholderOf(mPlaceHolder).error(mErrorHolder)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)).into(iPicture);
                iPicture.setTranslationX(( i%column )*( imageSize+mSpace ));
                iPicture.setTranslationY(( i/column )*( imageSize+mSpace ));
            }else {
                if(mAddMaskHolder != 0 && i == urlListSize) {
                    iPicture.setTag("addMask");
                    iPicture.setVisibility(View.VISIBLE);
                    iPicture.setImageResource(mAddMaskHolder);
                    iPicture.setTranslationX(( i%column )*( imageSize+mSpace ));
                    iPicture.setTranslationY(( i/column )*( imageSize+mSpace ));
                }else {
                    iPicture.setVisibility(View.GONE);
                }
            }

            if(i == MAX_DISPLAY_COUNT-1) {
                tOverflowCount.setTranslationX(( i%column )*( imageSize+mSpace ));
                tOverflowCount.setTranslationY(( i/column )*( imageSize+mSpace ));
            }
        }
        getLayoutParams().height = imageSize*row+mSpace*( row-1 );
    }

    @Override
    public void onClick(View v){
        if(mCallback != null) {
            mCallback.onThumbPictureClick((ImageView)v, mVisiblePictureList, mDataList);
        }
    }

    public interface Callback {
        /**
         *
         * @param i  i.getTag() notNull  mean add pic
         * @param imageGroupList
         * @param urlList
         */
        void onThumbPictureClick(ImageView i, List<ImageView> imageGroupList, List<String> urlList);
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        isInit = true;
        if(effectiveList(mDataList) && effectiveList(mThumbDataList) && mThumbDataList.size() == mDataList.size()) {
            notifyDataChanged();
        }
    }

    public W9PicsLayout2 setSingleMaxHSize(int singleMaxHSize){
        mSingleMaxHSize = singleMaxHSize;
        return this;
    }

    public W9PicsLayout2 setSingleMaxSize(int singleMaxSize){
        mSingleMaxSize = singleMaxSize;
        return this;
    }

    public W9PicsLayout2 setPlaceHolder(int placeHolder){
        mPlaceHolder = placeHolder;
        return this;
    }

    public W9PicsLayout2 setErrorHolder(int errorHolder){
        mErrorHolder = errorHolder;
        return this;
    }

    public W9PicsLayout2 setSpace(int space){
        mSpace = space;
        return this;
    }
}
