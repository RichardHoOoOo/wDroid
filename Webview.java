package com.android.commands.monkey;

import com.android.commands.monkey.Node;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Rect;

/*
 * Created by Jiajun on 28/03/2018
 * This class represent a WebView in the screen
 * A WebView is composed of a set of Nodes, and we only consider leaf Node
 */

public class Webview {
    
    List<Node> nodes;
    Rect bound;
    String structure; //a list of numbers to indicate the structure of the WebView, which corresponds to UiAutomatorViewer
    String visibility; //a list of 0/1 numbers to indicate the visibility of each element in WebView, where 0 means invisible and 1 means visible
    boolean containsVideo;
    
    
    public Webview() {
        nodes = new ArrayList<Node>();
        structure = "";
        visibility = "";
        containsVideo = false;
    }
    
    /*
     * if there is no nodes in webview, it is empty
     */
    public boolean isEmpty() {
        if(nodes.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public void setStructure(String structure) {
        this.structure = structure;
    }
    
    public String getStructure() {
        return this.structure;
    }
    
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    
    public String getVisibility() {
        return this.visibility;
    }
    
    public void setBound(Rect bound) {
        this.bound = bound;
    }
    
    public Rect getBound() {
        return this.bound;
    }
    
    public boolean addNode(Node node) {
        return nodes.add(node);
    }
    
    public List<Node> getNodes() {
        return nodes;
    }
    
    //"Has something" means WebView has displayed its major contents instead of loading message
    public boolean hasSomething() {
        int num = 0;
        for(Node node: nodes) {
            if(node.hasSomething()) num++;
            //A WebView has something only if there are more than 3 elements displayed in a WebView, 
            if(num > 3) return true;
        }
        return false;
    }
    
    public void print() {
        for(Node node: nodes) {
            node.print();
        }
    }
    
    public void setContainsVideo(boolean containsVideo) {
        this.containsVideo = containsVideo;
    }
    
    public boolean containsVideo() {
        return this.containsVideo;
    }
    
    public boolean hasVisibleNodes() {
        for(Node node: nodes) {
            if(node.isVisibleToUser()) return true;
        }
        return false;
    }
    
//    public boolean hasVisibleVideo() {
//        for(Node node: nodes) {
//            if(node.isVisibleVideoNode()) return true;
//        }
//        return false;
//    }
    
      /*
      * structure equals
      */
     @Override
     public boolean equals(Object o) {
         if(o == null) return false;
         if(! (o instanceof Webview)) return false;
         Webview other = (Webview) o;
         return this.structure.equals(other.structure);
     }
     
     public boolean structureAndVisibilityEquals(Object o) {
         if(o == null) return false;
         if(! (o instanceof Webview)) return false;
         Webview other = (Webview) o;
         return this.structure.equals(other.structure) && this.visibility.equals(other.visibility);
     }
    
     public void clearMappedNodes() {
         for(Node node: nodes) {
             node.setAleadyMapped(false);
         }
     }
    

//    /*
//     * Content equals
//     */
//    @Override
//    public boolean equals(Object o) {
//        if(o == null) return false;
//        if(! (o instanceof Webview)) return false;
//        Webview other = (Webview) o;
//        return this.nodes.equals(other.nodes);
//    }
    
    

}
