package com.trump.myxposed;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * just for test temp
 */
public class Test implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("Xposed");
        Log.d("Xposed", "trump xposed");
        if (!loadPackageParam.packageName.equals("com.tencent.mm"))
            return;

        Log.d("Xposed", "we are in wechat");


//        XposedHelpers.findAndHookMethod(“包名+类名”,
//                lpparam.classLoader, “要hook的函数名称”, 第一个参数类型, 第二个参数类型….., new
//                XC_MethodHook()

        final Class<?> pluginHelper = findClass("com.tencent.mm.ay.c", loadPackageParam.classLoader);
        findAndHookMethod(pluginHelper, "FZ", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
//                param.args[0]：得到被拦截方法的第一个参数，返回值是Object
//                param.getResult()：得到被拦截方法的执行结果，返回值是Object

                final String plugin = (String) param.args[0];
                if (plugin.equals("games")) {
                    if (BuildConfig.DEBUG) {
                        XposedBridge.log("dreamtobe change plugin[" + plugin + "] result to false!");
                    }
                    param.setResult(false);
                }
            }
        });
    }

}
