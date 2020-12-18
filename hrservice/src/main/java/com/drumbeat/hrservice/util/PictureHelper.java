package com.drumbeat.hrservice.util;

import android.app.Activity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.util.List;

/**
 * @author ZhangYuhang
 * @describe 图片选择
 * @date 2020/12/9
 * @updatelog
 */
public class PictureHelper {

    public static void selectAlbum(Activity activity, int count, List<LocalMedia> selected, final OnSelectListener onSelectListener) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .isGif(false)
                .maxSelectNum(count)
                .selectionData(selected)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        //上传图片
                        if (result != null && result.size() > 0) {
                            if (onSelectListener != null) {
                                onSelectListener.selected(result);
                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    public static void selectAlbum(Activity activity, int count, List<LocalMedia> selected, int intentCode) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .isGif(false)
                .maxSelectNum(count)
                .forResult(intentCode);
    }

    public interface OnSelectListener {
        void selected(List<LocalMedia> data);
    }

}
