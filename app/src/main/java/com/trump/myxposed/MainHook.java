package com.trump.myxposed;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * just for test temp
 */
public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

    public static final String PACKAGE_WECHAT = "com.tencent.mm";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
//        addFlagInSystemBar(loadPackageParam);
//        testHookRecruit(loadPackageParam);

        if (!loadPackageParam.packageName.equals(PACKAGE_WECHAT))
            return;

        Log.d("xposed", "in wechat now");

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                Class<?> hookclass = null;

                try {
                    Log.d("xposed", "load class start");
                    hookclass = cl.loadClass(PACKAGE_WECHAT + ".ui.conversation.BizConversationUI");
                } catch (Exception e) {
                    Log.e("xposed", "find class error ", e);
                    return;
                }
                Log.d("xposed", "find class success");
                hookWechat(hookclass, "onCreate");
            }
        });
    }

    public void hookWechat(Class classLoader, String methodName) {
        XposedHelpers.findAndHookMethod(classLoader, methodName, Bundle.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                Log.d("xposed", "hooked success");
                return null;
            }
        });
    }

    public void testHookRecruit(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.packageName.equals("com.libo.recruit"))
            return;

        Log.d("xposed", "we are now in recruit");

//        XposedHelpers.findAndHookMethod(“包名+类名”,
//                lpparam.classLoader, “要hook的函数名称”, 第一个参数类型, 第二个参数类型….., new
//                XC_MethodHook()

        Class<?> pluginHelper = findClass("com.libo.recruit.ui.activity.AboutUsActivity", loadPackageParam.classLoader);
        findAndHookMethod(pluginHelper, "setText", TextView.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
//                param.args[0]：得到被拦截方法的第一个参数，返回值是Object
//                param.getResult()：得到被拦截方法的执行结果，返回值是Object
                try {
                    Log.d("xposed", param.args[0].toString());
                    Log.d("xposed", param.thisObject.toString());

                    TextView tv2 = (TextView) param.args[0];
                    tv2.setText("Hook Success!");

                } catch (Exception e) {

                }
            }
        });
    }

    public void addFlagInSystemBar(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.packageName.equals("com.android.systemui"))
            return;

        try {
            findAndHookMethod("com.android.systemui.statusbar.policy.Clock",
                    loadPackageParam.classLoader, "updateClock", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            TextView tv = (TextView) param.thisObject;
                            String text = tv.getText().toString();
                            tv.setText("Trump " + text);
                        }
                    });
        } catch (Exception e) {
            Log.d("Xposed", "exception trump");
        }
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
