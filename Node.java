package com.android.commands.monkey;

/*
 * Created by Jiajun on 28/03/2018
 * This class represent a node in WebView, where all the fields are identical
 * to the attribute provided by UiAutomatorViewer except "index" and "bounds" 
 * because they are not necessary in our application.
 * Note: each field can be null!
 */

public class Node {
   
    private String text;
    private String resource_id;
    private String className;
    private String packageName;
    private String contentDesc;
    private boolean checkable;
    private boolean checked;
    private boolean clickable;
    private boolean enabled;
    private boolean focusable;
    private boolean focused;
    private boolean scrollable;
    private boolean long_clickable;
    private boolean password;
    private boolean selected;
    private boolean isVisibleToUser;
    private boolean alreadyMapped;
    
    public void setAleadyMapped(boolean alreadyMapped) {
        this.alreadyMapped = alreadyMapped;
    }
    
    public boolean isAlreadyMapped() {
        return this.alreadyMapped;
    }
    
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getResource_id() {
        return resource_id;
    }
    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getContentDesc() {
        return contentDesc;
    }
    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }
    public boolean isCheckable() {
        return checkable;
    }
    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public boolean isClickable() {
        return clickable;
    }
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean isFocusable() {
        return focusable;
    }
    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }
    public boolean isFocused() {
        return focused;
    }
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    public boolean isScrollable() {
        return scrollable;
    }
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
    public boolean isLong_clickable() {
        return long_clickable;
    }
    public void setLong_clickable(boolean long_clickable) {
        this.long_clickable = long_clickable;
    }
    public boolean isPassword() {
        return password;
    }
    public void setPassword(boolean password) {
        this.password = password;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isVisibleToUser() {
        return isVisibleToUser;
    }
    public void setVisibleToUser(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
    }
    
    public void print() {
        if(!contentDesc.equals("")) System.out.println(contentDesc + ": " + isVisibleToUser);
    }
    
    public boolean hasSomething() {
        if(!this.className.equals(MonkeyView.WEBVIEW_CLASSNAME) && !this.contentDesc.equals("")) {
            return true;
        } else {
            return false;
        }
    }
    
//    public boolean isVisibleVideoNode() {
//        for(int i=0; i<MonkeyView.VIDEO_TAG_LIST.length; i++) {
//            if(this.contentDesc.equals(MonkeyView.VIDEO_TAG_LIST[i]) && this.isVisibleToUser) return true;
//        }
//        return false;
//    }
    
    @Override
    public boolean equals(Object o) {
       if(o == null) return false;
       if(! (o instanceof Node)) return false;
       Node other = (Node) o;
       //remove all the white spaces in text and content description
       if(this.text.replaceAll("\\s","").equals(other.text.replaceAll("\\s","")) && this.resource_id.equals(other.resource_id)
             && this.className.equals(other.className) && this.packageName.equals(other.packageName)
             && this.contentDesc.replaceAll("\\s","").equals(other.contentDesc.replaceAll("\\s","")) && this.checkable == other.checkable
             && this.checked == other.checked && this.clickable == other.clickable 
             && this.enabled == other.enabled && this.focusable == other.focusable
             && this.focused == other.focused && this.scrollable == other.scrollable
             && this.long_clickable == other.long_clickable && this.password == other.password
             && this.selected == other.selected) {
           return true;
       } else {
           return false;
       }
    }
    
    
}