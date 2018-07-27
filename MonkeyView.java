package com.android.commands.monkey;

import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.view.accessibility.AccessibilityNodeInfo;
import android.webkit.WebView;

import com.android.commands.monkey.Node;
import com.android.commands.monkey.Screen;
import com.android.commands.monkey.Webview;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.os.HandlerThread;
import android.app.IActivityManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioSystem;



/*
 * Created by Jiajun on 27/03/2018.
 * This class is the extentison of Monkey we made
 * It encloses the following functions:
 *  1. Get the UI hierarchy of the currently displayed screen by UIAutomator 
 *  2.
 *  3.
 */
public class MonkeyView {
    
    public static final String APPS_LIST = "Apps";
    public static final String WEBVIEW_CLASSNAME = "android.webkit.WebView";
    public static final String RECYCLER_VIEW = "android.support.v7.widget.RecyclerView";
    public static final int MAX_ATTEMPS = 10000;
    public static final int MAX_WAITING_TIME_FOR_HASSOMETHING = 5000; //5s
    public static final int MAX_WAITING_TIME_FOR_FINISHLOADING = 10000; //10s
    public static final int MAX_WAITING_TIME_FOR_NOTHING_CHANGED = 1500; //1.5s
    public static final String[] VIDEO_TAG_LIST = {"YouTube Video Player", "video"};
    public static final int SCREEN_LOCK = 1;
    public static final int PRESS_HOME = 2;
    public static final int ROTATION = 3;
    public static final int SCREEN_INCONSISTENT = 1;
    public static final int HAS_SOUND = 2;
    public static final String UIAUTOMATION_HANDLER_THREAD = "UiAutomationHandlerThread";
    private static UiAutomation uiAutomation;
    private static HandlerThread handlerThread = new HandlerThread(UIAUTOMATION_HANDLER_THREAD);
    public static String SCREENSHOTS_FOLDER = "/data/local/tmp/";
    private static String packageName;
    private static String appName;
    private static AccessibilityNodeInfo recyclerView;
    public static boolean isInAppPackage = false;
    
    //Initialize UiAutomation
    public static void setup(String pName, String aName) { 
        handlerThread.setDaemon(true);
        handlerThread.start();
        uiAutomation = new UiAutomation(handlerThread.getLooper(), new UiAutomationConnection());
        uiAutomation.connect();
        packageName = pName;
        appName = aName;
        SCREENSHOTS_FOLDER = SCREENSHOTS_FOLDER + packageName + "_report";
        File screenshotFolder = new File(SCREENSHOTS_FOLDER);
        if(!screenshotFolder.exists()) {
            if(!screenshotFolder.mkdirs()){
                System.err.println("Could not create monkey screenshot folder");
            }
        }
        File bugreport = new File(SCREENSHOTS_FOLDER + "/bug_report.txt");
        if(!bugreport.exists()) {
            try {
                if(!bugreport.createNewFile()){
                    System.err.println("Could not create bug_report.txt");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        try {
            FileWriter fw = new FileWriter(SCREENSHOTS_FOLDER + "/bug_report.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Bug report for [" + appName + "] ("+  packageName + "):");
            bw.newLine();
            bw.close();
            fw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * return true if the screen has video tag and the music if active
     */
    public static boolean hasSoundFromVideo(Screen screen) {
        if(screen.hasVideo() && isMusicActive()) return true;
        else return false;
    }
    
    /*
     * return true if detect music playing
     */
    public static boolean isMusicActive() {
        return AudioSystem.isStreamActive(AudioSystem.STREAM_MUSIC, 0);
    }
    
    
    /*
     * Check whether screenA and screenB are consistent
     */
    public static boolean isConsistent(Screen screenA, Screen screenB) {
        if(!screenA.hasWebView() && !screenB.hasWebView()) return true; // If both screen A and B do not have webview, return true
        if(!screenA.hasWebView() || !screenB.hasWebView()) return false;
        if(screenA.isEmpty() && screenB.isEmpty()) return true;
        if(screenA.isEmpty() || screenB.isEmpty()) return false;
        if(!screenA.hasVisibleWebView() && !screenB.hasVisibleWebView()) return true;
        if(!screenA.hasVisibleWebView() || !screenB.hasVisibleWebView()) return false;
        boolean allFound = true;
        boolean atLeastOneVisible = false;
        List<Webview> webviewsInA = screenA.getWebViews();
        if(screenA.structureEqual(screenB)) {
            List<Webview> webviewsInB = screenB.getWebViews();
            Tag1:
            for(int i=0; i<webviewsInA.size(); i++) {
                Webview webviewInA = webviewsInA.get(i);
                List<Node> nodesInA = webviewInA.getNodes();
                Webview webviewInB = webviewsInB.get(i);
                List<Node> nodesInB = webviewInB.getNodes();
                for(int j=0; j<nodesInA.size(); j++) {
                    Node nodeInA = nodesInA.get(j);
                    if(nodeInA.isVisibleToUser()) {
                        Node nodeInB = nodesInB.get(j);
                        if(!nodeInA.equals(nodeInB)) {
                            allFound = false;
                            break Tag1;
                        } else {
                            if(nodeInB.isVisibleToUser()) atLeastOneVisible = true;
                        }
                    }
                }
            }
        } else {
            screenA.clearMappedNodes();
            screenB.clearMappedNodes();
            Tag1:
            for(Webview webviewInA: webviewsInA) {
                List<Node> nodesInA = webviewInA.getNodes();
                for(Node nodeInA: nodesInA) {
                    if((!nodeInA.getContentDesc().equals("")) && nodeInA.isVisibleToUser()) {
                        //for each visible node in screen A, we must find the corresponding node in screen B
                        boolean found = false;
                        List<Webview> webviewsInB = screenB.getWebViews();
                        Tag2:
                        for(Webview webviewInB: webviewsInB) {
                            List<Node> nodesInB = webviewInB.getNodes();
                            for(Node nodeInB: nodesInB) {
                                if(!nodeInB.isAlreadyMapped() && nodeInA.equals(nodeInB)) {
                                    found = true;
                                    nodeInB.setAleadyMapped(true);
                                    if(nodeInB.isVisibleToUser()) atLeastOneVisible = true;
                                    break Tag2;
                                }
                            }
                        }
                        if(!found) {
                           allFound = false;
                           break Tag1;
                        } 
                    }
                }
            }
        }
        if(allFound && atLeastOneVisible) return true;
        else return false;
    }
    
    /*
     * get the root node of the current window, have no idea why root can be null
     */
    private static AccessibilityNodeInfo getRoot() {
        int attemps = 0;
        AccessibilityNodeInfo root = null;
        while(attemps < MAX_ATTEMPS) {
            root = uiAutomation.getRootInActiveWindow();
            if(root == null) {
                attemps ++;
            } else {
                break;
            }
        }
        if(root == null) System.out.println("Warning: root is null!");
        return root;
    }
    
    

    /*
     * get the ui layout of current screen, return true if root is not null
     */
    public static boolean getUILayout(Screen screen) {
        sleep(500);
        Screen currentScreen = new Screen();
        AccessibilityNodeInfo root = getRoot();
        if(root != null) {
            if(root.getPackageName() != null) {
                String currentPackage = root.getPackageName().toString();
                if(currentPackage.equals(packageName)) {
                    isInAppPackage = true;
                } else {
                    isInAppPackage = false;
                    return true;
                }
            }
            travaseUITree(root, currentScreen, null, false, null, 0, null);
            //Firstly, check whether the current screen has WebView
            if(currentScreen.hasWebView()) {
                //Secondly, give at most 5s to let WebView load something
                long startTime = System.nanoTime();
                long elapsedTime = 0;
                while((elapsedTime / 1000000) < MAX_WAITING_TIME_FOR_HASSOMETHING) {
                    if(currentScreen.hasSomething()) {
                        break;
                    } else {
                        root = getRoot();
                        currentScreen = new Screen();
                        if(root != null) {
                            travaseUITree(root, currentScreen, null, false, null, 0, null);
                        }
                    }
                    elapsedTime = System.nanoTime() - startTime;
                }
                //Thirdly, give WebView at most 10s to finish loading.
                //If WebView content hasn't change for 1500ms, we think it has finished loading.
                //If WebView content changes, wait another 1500ms.
                if(currentScreen.hasSomething()) {
                    startTime = System.nanoTime();
                    long noChangeTime = startTime;
                    elapsedTime = 0;
                    while((elapsedTime / 1000000) < MAX_WAITING_TIME_FOR_FINISHLOADING) {
                        root = getRoot();
                        Screen nextScreen = new Screen();
                        if(root != null) {
                            travaseUITree(root, nextScreen, null, false, null, 0, null);
                            if(nextScreen.equals(currentScreen)) {
                                //If WebView content hasn't changed for 1.5s, break the loop
                                if(((System.nanoTime() - noChangeTime) / 1000000) >= MAX_WAITING_TIME_FOR_NOTHING_CHANGED) {
                                    break;
                                }
                            } else {
                                //If WebView content changed, update the currentScreen and wait another 500ms
                                currentScreen = nextScreen;
                                noChangeTime = System.nanoTime();
                            }
                        }
                        elapsedTime = System.nanoTime() - startTime;
                    }
                }
                currentScreen.setScreenshot(takeScreenshot());
                screen.copy(currentScreen);
            } else {
                screen.setScreenshot(takeScreenshot());
            }
            return true;
        } else {
            return false;
        }
    }
    
    /*
     * return the current screenshot
     */
    public static Bitmap takeScreenshot() {
        Bitmap bitmap = null;
        int attempt = 0;
        while(attempt < MAX_ATTEMPS) {
            bitmap = uiAutomation.takeScreenshot();
            if(bitmap == null) {
                attempt ++;
            } else {
                break;
            }
        }
        return bitmap;
    }
    
    
    /*
     * Turn screen off then on
     * return true means detect sound
     */
    public static boolean turnScreenOffAndOn(boolean hasSoundFromVideo) {
        boolean detectSound = false;
        try{
            String[] strings = {"input", "keyevent", "26"};//26 is the key code of power
            Runtime.getRuntime().exec(strings).waitFor();
            System.out.println("Turn screen off.");
            sleep(500);
            if(hasSoundFromVideo) {
                if(isMusicActive()) detectSound = true;
            }
            Runtime.getRuntime().exec(strings).waitFor();
            System.out.println("Turn screen on.");
            return detectSound;
        } catch(IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return detectSound;
    }
    
    
    /*
     * rotate screen to the orientation specified by i
     */
    public static void rotateScreen(int i) {
        uiAutomation.setRotation(i);
        System.out.println("Rotate screen.");
        sleep(1000);
    }
    
    
    /*
     * Press home button and then re-enter the app
     * return true means detect sound
     */
    public static boolean pressHomeThenGoBack(boolean hasSoundFromVideo) {
        boolean detectSound = false;
        try{
            String[] strings = {"input", "keyevent", "3"};//3 is the key code of home
            //press home button
            Runtime.getRuntime().exec(strings).waitFor();
            System.out.println("Press Home.");
        } catch(IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sleep(500);
        //click App list
        AccessibilityNodeInfo root = getRoot();
        if(root != null) {
            traverseUITreeToFindAppList(root, APPS_LIST);
        }
        sleep(500);
        if(hasSoundFromVideo) {
            if(isMusicActive()) detectSound = true;
        }
        boolean foundApp = false;
        //Find the app we want in App list
        //If found, click to open the app. Otherwise, scroll App list to keep searching
        int attemps = 0;
        while(!foundApp) {
            if(attemps >= MAX_ATTEMPS) break;
            recyclerView = null;
            AccessibilityNodeInfo appList = getRoot();
            if(appList != null) {
                if(traverseUITreeToFindApp(appList)) {
                    foundApp = true;
                } else {
                    if(recyclerView != null) {
                        recyclerView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        System.out.println("Scroll screen to find app.");
                    }
                }
            }
            attemps ++;
        }
        return detectSound;
    } 
    
    /*
     * This method is invoked when the current screen is the home screen, 
     * and we need to find APP List and click it.
     */
    private static boolean traverseUITreeToFindAppList(AccessibilityNodeInfo root, String appList) {
        if(root.getContentDescription() != null && root.getContentDescription().toString().equals(appList)) {
            root.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            System.out.println("Click App List.");
            return true;
        }
        for(int i=0; i<root.getChildCount(); i++) {
            AccessibilityNodeInfo child = getChild(root, i);
            if(child != null) {
                if(traverseUITreeToFindAppList(child, appList)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /*
     * This method is invoked when we are in the APP List screen,
     * and we need to find the App and click it.
     */
    private static boolean traverseUITreeToFindApp(AccessibilityNodeInfo root) {
        if(root.getText() != null && root.getText().toString().equals(appName)) {
            root.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            System.out.println("Click " + appName);
            return true;
        }
        if(root.getClassName() != null && root.getClassName().toString().equals(RECYCLER_VIEW)) {
            recyclerView = root;
        }
        for(int i=0; i<root.getChildCount(); i++) {
            AccessibilityNodeInfo child = getChild(root, i);
            if(child != null) {
                if(traverseUITreeToFindApp(child)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    
    /*
     * Traverse UI tree to construct nodes in WebView if exists in the current screen
     */
    private static void travaseUITree(AccessibilityNodeInfo root, Screen screen, Webview webview, boolean inWebView, StringBuilder structure, int index, StringBuilder visibility) {
        if(inWebView == false) {
            //not yet in a WebView, so webview is null and inWebView is false now.
            if(root.getClassName() != null && root.getClassName().toString().equals(WEBVIEW_CLASSNAME)) {
                //encounter a WebView
                Webview wv = new Webview();
                Rect rect = new Rect();
                root.getBoundsInScreen(rect);
                wv.setBound(rect);
                screen.setHasWebView(true);
                StringBuilder sb = new StringBuilder();
                StringBuilder vb = new StringBuilder();
                for(int i=0; i<root.getChildCount(); i++) {
                    //set inWebView to be true because we are going to traverse the childern of WebView,
                    //which means we are going to be inside WebView.
                    AccessibilityNodeInfo child = getChild(root, i);
                    if(child != null) travaseUITree(child, screen, wv, true, sb, i, vb);
                }
                wv.setStructure(sb.toString());
                wv.setVisibility(vb.toString());
                screen.addWebview(wv);
            } else {
                //otherwise, keep traversing normally
                for(int i=0; i<root.getChildCount(); i++) {
                    AccessibilityNodeInfo child = getChild(root, i);
                    if(child != null) travaseUITree(child, screen, null, false, null, 0, null);
                }
            }
        } else {
            //append index to structure
            structure.append(index + " ");
            //check whether the current node is visible to user
            Rect r = new Rect();
            root.getBoundsInScreen(r);
            boolean isVisbile = false;
            if(r.intersect(webview.getBound())) {
                isVisbile = true;
                visibility.append(1);
            } else {
                visibility.append(0);
            }
            if(isVideoNode(root)) webview.setContainsVideo(true);
            //now we are in the WebView, so webview is not null now.
            //if the node is leaf node or is a video node, add to the webview layout, which means we do not further explore child nodes of video node.
             if(root.getChildCount() == 0 || isVideoNode(root)) {
                //leaf node in WebView
                Node node = new Node();
                node.setText(root.getText() == null? "": root.getText().toString().trim());
                node.setResource_id(root.getViewIdResourceName() == null? "": root.getViewIdResourceName().trim());
                node.setClassName(root.getClassName() == null? "": root.getClassName().toString().trim());
                node.setPackageName(root.getPackageName() == null? "": root.getPackageName().toString().trim());
                node.setContentDesc(root.getContentDescription() == null? "": root.getContentDescription().toString().trim());
                node.setCheckable(root.isCheckable());
                node.setChecked(root.isChecked());
                node.setClickable(root.isClickable());
                node.setEnabled(root.isEnabled());
                node.setFocusable(root.isFocusable());
                node.setScrollable(root.isScrollable());
                node.setLong_clickable(root.isLongClickable());
                node.setPassword(root.isPassword());
                node.setSelected(root.isSelected());
//                Rect rect = new Rect();
//                root.getBoundsInScreen(rect);
                node.setVisibleToUser(isVisbile);
                node.setAleadyMapped(false);
                webview.addNode(node);
            } else {
                //otherwise, keep traversing normally
                for(int i=0; i<root.getChildCount(); i++) {
                    AccessibilityNodeInfo child = getChild(root, i);
                    if(child != null) travaseUITree(child, screen, webview, true, structure, i, visibility);
                }
            }
        }
    }
    
    /*
     * let monkey sleep
     */
    public static void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * get the i-th child of root
     */
    private static AccessibilityNodeInfo getChild(AccessibilityNodeInfo root, int i) {
        int attemps = 0;
        AccessibilityNodeInfo child =null;
        while(attemps < MAX_ATTEMPS) {
            child = root.getChild(i);
            if(child == null) {
                attemps ++;
            } else {
                break;
            }
        }
        if(child == null) System.out.println("Warning: A child is null!");
        return child;
    }
    
    
    /*
     * Check whether node is a video node
     */
    private static boolean isVideoNode(AccessibilityNodeInfo node) {
        boolean isVideo = false;
        if(node.getContentDescription() != null) {
            String contentDesc = node.getContentDescription().toString().trim();
            for(int i=0 ; i<VIDEO_TAG_LIST.length; i++) {
                if(contentDesc.equals(VIDEO_TAG_LIST[i])) {
                    isVideo = true;
                    break;
                }
            }
        }
        return isVideo;
    }
    
    /*
     * report bug according to bug code
     */
    public static void reportBug(int bugCode, Screen before, Screen after, int bugId, int eventType) {
        String note = "";
        String event = "";
        switch(eventType) {
            case SCREEN_LOCK: 
                event = "Lock screen";
                break;
            case PRESS_HOME:
                event = "Press home button";
                break;
            case ROTATION:
                event = "Rotate screen";
                break;
        }
        try {
            if(before.getScreenshot() != null) {
                File beforeFile = new File(SCREENSHOTS_FOLDER + "/bug" + bugId + "-before.png");
                FileOutputStream fos = new FileOutputStream(beforeFile);
                before.getScreenshot().compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } else {
                note = " Note: screenshot before get lost!";
            }
            if(after.getScreenshot() != null) {
                File afterFile = new File(SCREENSHOTS_FOLDER + "/bug" + bugId + "-after.png");
                FileOutputStream fos = new FileOutputStream(afterFile);
                after.getScreenshot().compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } else {
                note = note + " Screenshot after get lost!";
            }
            FileWriter fw = new FileWriter(SCREENSHOTS_FOLDER + "/bug_report.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            switch(bugCode) {
                case SCREEN_INCONSISTENT:
                    System.out.println("===============================Bug " + bugId + "===============================");
                    System.out.println("Bug Id " + bugId + ": Screen Inconsistence Found! caused by " + event);
                    System.out.println("Current activity: " + Monkey.currentActivity);
                    System.out.println("=============================================================");
                    bw.write("BUG: Screen Inconsistence Found caused by " + event + "; Bug id: " + bugId + "; Activity: " + Monkey.currentActivity + note);
                    bw.newLine();
                    break;
                case HAS_SOUND:
                    System.out.println("===============================Bug " + bugId + "===============================");
                    System.out.println("Bug Id " + bugId + ": SOUND From Video Detected! caused by " + event);
                    System.out.println("Current activity: " + Monkey.currentActivity);
                    System.out.println("=============================================================");
                    bw.write("BUG: SOUND From Video Detected caused by " + event + "; Bug id:" + bugId + "; Activity: " + Monkey.currentActivity + note);
                    bw.newLine();
                    break;
                default:
                    break;     
            }
            bw.close();
            fw.close();
            before.print();
            after.print();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
