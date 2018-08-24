package com.android.commands.monkey;

import android.R.anim;
import android.graphics.Bitmap;

import com.android.commands.monkey.Webview;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Jiajun on 28/03/2018
 * This class represent a Screen, but we only consider all the Webviews
 */

public class Screen {
    List<Webview> webviews;
    boolean containsWebView;
    boolean childIsNull;
    Bitmap screenshot; //indicate whether a child in the UI model is null when building it
    
    public Screen() {
        webviews = new ArrayList<Webview>();
        containsWebView = false;
        childIsNull = false;
        screenshot = null;
    }
    
    public boolean isChildIsNull() {
        return this.childIsNull;
    }
    
    public void setChildIsNull(boolean childIsNull) {
        this.childIsNull = childIsNull;
    }
    
    public boolean hasVisibleWebView() {
        for(Webview webview: webviews) {
            if(webview.hasVisibleNodes()) {
                return true;
            }
        }
        return false;
    }
    
    public void setScreenshot(Bitmap screenshot) {
        this.screenshot = screenshot;
    }
    
    public Bitmap getScreenshot() {
        return this.screenshot;
    }
    
    //If Screen contains WebView instance, set true
    public void setHasWebView(boolean containsWebView) {
        this.containsWebView = containsWebView;
    }
    
    //If Screen contains WebView, return true
    public boolean hasWebView() {
        return containsWebView;
    }
    
    public List<Webview> getWebViews() {
        return webviews;
    }
    
    public void clearMappedNodes() {
        for(Webview webview: webviews) {
            webview.clearMappedNodes();
        }
    }
    
    public boolean isEmpty() {
        for(Webview webview: webviews) {
            if(!webview.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean structureEqual(Screen other) {
        boolean isEqual = true;
        if(this.webviews.size() != other.webviews.size()) isEqual = false;
        else {
            for(int i=0; i<this.webviews.size(); i++) {
                if(!this.webviews.get(i).getStructure().equals(other.webviews.get(i).getStructure())) {
                    isEqual = false;
                    break;
                }
            }
        }
        return isEqual;
    }
    
    //make this identical to other screen
    public void copy(Screen otherScreen) {
        this.webviews.clear();
        this.containsWebView = false;
        this.childIsNull = false;
        this.screenshot = null;
        for(Webview otherWebview: otherScreen.getWebViews()) {
            Webview webview = new Webview();
            webview.setBound(otherWebview.getBound());
            webview.setStructure(otherWebview.getStructure());
            webview.setVisibility(otherWebview.getVisibility());
            webview.setContainsVideo(otherWebview.containsVideo());
            for(Node otherNode: otherWebview.getNodes()) {
                Node node = new Node();
                node.setText(otherNode.getText());
                node.setResource_id(otherNode.getResource_id());
                node.setClassName(otherNode.getClassName());
                node.setPackageName(otherNode.getPackageName());
                node.setContentDesc(otherNode.getContentDesc());
                node.setCheckable(otherNode.isCheckable());
                node.setChecked(otherNode.isChecked());
                node.setClickable(otherNode.isClickable());
                node.setEnabled(otherNode.isEnabled());
                node.setFocusable(otherNode.isFocusable());
                node.setScrollable(otherNode.isScrollable());
                node.setLong_clickable(otherNode.isLong_clickable());
                node.setPassword(otherNode.isPassword());
                node.setSelected(otherNode.isSelected());
                node.setVisibleToUser(otherNode.isVisibleToUser());
                webview.addNode(node);
            }
            this.webviews.add(webview);
        }
        this.containsWebView = otherScreen.hasWebView();
        this.childIsNull = otherScreen.isChildIsNull();
        this.screenshot = otherScreen.screenshot;
    }
    
    public boolean hasSomething() {
        for(Webview webview: webviews) {
            if(webview.hasSomething()) return true;
        }
        return false;
    }
    
    public boolean hasVideo() {
        for(Webview webview: webviews) {
            if(webview.containsVideo()) return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(! (o instanceof Screen)) return false;
        Screen other = (Screen) o;
        if(!other.hasWebView()) return false;
        return this.webviews.equals(other.webviews);
    }
    
    public boolean structureAndVisibilityEquals(Object o) {
        if(o == null) return false;
        if(! (o instanceof Screen)) return false;
        Screen other = (Screen) o;
        if(!other.hasWebView()) return false;
        if(this.webviews.size() != other.webviews.size()) return false;
        for(int i=0; i<this.webviews.size(); i++) {
            if(!this.webviews.get(i).structureAndVisibilityEquals(other.webviews.get(i))) return false;
        }
        return true;
    }
    

    public boolean addWebview(Webview webview) {
        return webviews.add(webview);
    }
    
    public void print() {
        System.out.println("=================================================");
        for(Webview webview: webviews) {
            System.out.println("------------------------------------------------");
            webview.print();
            System.out.println("------------------------------------------------");
        }
        System.out.println("=================================================");
    }
}
