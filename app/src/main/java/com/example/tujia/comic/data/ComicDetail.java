package com.example.tujia.comic.data;

import java.io.Serializable;
import java.util.List;

import com.example.tujia.comic.util.CollectionUtil;
import com.google.common.collect.Lists;

/**
 * Detail of comic
 *
 * @author wlei 2012-5-29
 */
public class ComicDetail implements Serializable {

  /**
   * Image info of SplitImage mode.
   */
  public static class StoryLine implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * x point to display.
     */
    private float dis_p_x;
    /**
     * y point to display.
     */
    private float dis_p_y;
    /**
     * width of image to display.
     */
    private float dis_width;
    /**
     * height of image to display.
     */
    private float dis_height;

    public StoryLine() {
    }

    public StoryLine(float x, float y, float w, float h) {
      dis_p_x = x;
      dis_p_y = y;
      dis_width = w;
      dis_height = h;
    }

    public void setX(float x) {
      dis_p_x = x;
    }

    public float getX() {
      return dis_p_x;
    }

    public void setY(float y) {
      dis_p_y = y;
    }

    public float getY() {
      return dis_p_y;
    }

    public void setWidth(float width) {
      dis_width = width;
    }

    public float getWidth() {
      return dis_width;
    }

    public void setHeight(float height) {
      dis_height = height;
    }

    public float getHeight() {
      return dis_height;
    }

    /**
     * Returns a new {@link com.example.tujia.comic.data.ComicDetail.StoryLine} that resized.
     */
    StoryLine resize(int sampleSize) {
      final StoryLine storyLine = new StoryLine();
      storyLine.dis_p_x = dis_p_x / sampleSize;
      storyLine.dis_p_y = dis_p_y / sampleSize;
      storyLine.dis_width = dis_width / sampleSize;
      storyLine.dis_height = dis_height / sampleSize;
      return storyLine;
    }
  }

  public static class ImageInfo implements Serializable, Comparable<ImageInfo> {

    private static final long serialVersionUID = 1L;

    private String current_img_url;
    private int step_cnt;
    private List<StoryLine> storyline;
    /**
     * start with 1
     */
    private int localIndex;
    private int size;


    public int getSize() {
      return size;
    }


    public void setSize(int size) {
      this.size = size;
    }

    /**
     * Returns a new {@link com.example.tujia.comic.data.ComicDetail.ImageInfo} that resized.
     */
    public ImageInfo resize(int sampleSize) {
      final ImageInfo info = new ImageInfo();
      info.current_img_url = current_img_url;
      info.step_cnt = step_cnt;
      if (!CollectionUtil.isNullOrEmpty(storyline)) {
        info.storyline = Lists.newArrayList();
        for (final StoryLine story : storyline) {
          info.storyline.add(story.resize(sampleSize));
        }
      }
      return info;
    }

    public String getImageUrl() {
      return current_img_url;
    }

    public void setImageUrl(String current_img_url) {
      this.current_img_url = current_img_url;
    }

    public int getStepCount() {
      return step_cnt;
    }

    public void setStepCount(int step_cnt) {
      this.step_cnt = step_cnt;
    }

    public List<StoryLine> getStoryLine() {
      return storyline;
    }

    public void setStoryLine(List<StoryLine> storyline) {
      this.storyline = storyline;
    }

    public int getLocalIndex() {
      return localIndex;
    }

    public void setLocalIndex(int localIndex) {
      this.localIndex = localIndex;
    }

    @Override
    public int compareTo(ImageInfo another) {
      return localIndex - another.localIndex;
    }
  }

  private static final long serialVersionUID = 1L;

  private List<ImageInfo> img_infos;

  public List<ImageInfo> getImageInfos() {
    return img_infos;
  }
}
