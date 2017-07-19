package com.trump.myxposed;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * just for test temp
 */
public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

    public static final String PACKAGE_WECHAT = "com.tencent.mm";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals(PACKAGE_WECHAT))
            return;
        XposedHelpers.findAndHookMethod(Application.class,
                "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                        shieldSpecialChat(cl);

                        shieldMoments(cl);
                    }
                });
    }

    private void shieldSpecialChat(ClassLoader cl) {
        Class<?> hookclass = null;
        String className = PACKAGE_WECHAT + ".s.o";
        String fun = "eX";

        try {
            hookclass = cl.loadClass(className);
        } catch (Exception e) {
            Log.e("xposed", "find class error ", e);
            return;
        }

        XposedHelpers.findAndHookMethod(hookclass, fun, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String paramStr = param.args[0].toString();

                        //需要屏蔽的聊天对象的id  可以是好友、群、公众号、、、、、
                        String orginStr1 = "2342477308@chatroom";  //鸡年行大运
                        String orginStr2 = "1730967485@chatroom";  //苟富贵
                        String orginStr3 = "1072389255@chatroom";  //秘密基地的id

                        boolean finalBool = judgeArg(paramStr, orginStr1, orginStr2, orginStr3);
                        param.setResult(finalBool);

//                        Log.d("xposed", "入参----" + paramStr);
//                        Log.d("xposed", "hook success!!!   return=" + finalBool);
                    }
                });
    }

    public void shieldMoments(final ClassLoader cl) {
        Class<?> hookclass = null;
        String className = PACKAGE_WECHAT + ".plugin.sns.ui.En_424b8e16";
        String fun = "onCreate";

        try {
            hookclass = cl.loadClass(className);
        } catch (Exception e) {
            Log.e("xposed", "find class error ", e);
            return;
        }

        XposedHelpers.findAndHookMethod(hookclass, fun,
                Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            Activity context = (Activity) param.thisObject;
                            context.finish();
                        } catch (Exception e) {
                            Log.e("xposed", "hook exception=" + e);
                        }
                        Log.d("xposed", "hook success");
                    }
                });
    }

    public boolean judgeArg(String param, String... args) {
        boolean returnBool = false;

        for (String arg : args) {
            if (arg.equals(param)) {
                returnBool = true;
                break;
            }
        }

        return returnBool;
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        //first
        Log.d("Xposed", "initZygote");
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) throws Throwable {

        if (!initPackageResourcesParam.packageName.equals("com.tencent.mm"))
            return;


    }
}
