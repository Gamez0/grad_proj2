/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.praylist.socialcomponents.model;

import com.praylist.socialcomponents.enums.ItemType;
import com.praylist.socialcomponents.utils.FormatterUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Post implements Serializable, LazyLoading {

    private String id;
    private String title;
    private String username;
    private String description;
    private long createdDate;
    //private String imagePath;
    private String imageTitle;
    private String authorId;
    private long commentsCount;
    private long likesCount;
    private long watchersCount;
    private boolean hasComplain;
    private String prayerFor;
    private String prayerForId;
    private ArrayList<String> coPrayer;
    private boolean isGlobal;   // 전체공개인가? default는 전체공개로!
    private long emotionType=0;    // 0Joy 1Sadness 2Fear 3Anger 4Admiration
    private ItemType itemType;


    public Post() {
        this.createdDate = new Date().getTime();
        itemType = ItemType.ITEM;
    }

    public Post(ItemType itemType) {
        this.itemType = itemType;
        setId(itemType.toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(long watchersCount) {
        this.watchersCount = watchersCount;
    }

    public String getPrayerFor() {
        return prayerFor;
    }

    public void setPrayerFor(String prayerFor) {
        this.prayerFor = prayerFor;
    }

    public ArrayList<String> getCoPrayer() {
        return coPrayer;
    }

    public void setCoPrayer(ArrayList<String> coPrayer) {
        this.coPrayer = coPrayer;
    }

    public boolean isHasComplain() {
        return hasComplain;
    }

    public void setHasComplain(boolean hasComplain) {
        this.hasComplain = hasComplain;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public String getPrayerForId() {
        return prayerForId;
    }

    public void setPrayerForId(String prayerForId) {
        this.prayerForId = prayerForId;
    }

    public long getEmotionType() {
        return emotionType;
    }

    public void setEmotionType(long emotionType) {
        this.emotionType = emotionType;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("title", title);
        result.put("username",username);
        result.put("description", description);
        result.put("createdDate", createdDate);
        //result.put("imagePath", imagePath);
        result.put("imageTitle", imageTitle);
        result.put("authorId", authorId);
        result.put("commentsCount", commentsCount);
        result.put("likesCount", likesCount);
        result.put("watchersCount", watchersCount);
        result.put("hasComplain", hasComplain);
        result.put("prayerFor",prayerFor);
        result.put("prayerForId",prayerForId);
        result.put("coPrayer",coPrayer);
        result.put("isGlobal",isGlobal);
        result.put("emotionType",emotionType);
        result.put("createdDateText", FormatterUtil.getFirebaseDateFormat().format(new Date(createdDate)));
        return result;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public void setItemType(ItemType itemType) {

    }
}
