package com.trump.myxposed;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import java.util.List;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * just for test temp
 */
public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
//        addFlagInSystemBar(loadPackageParam);
//        testMultiDexHook(loadPackageParam);
        testHookRecruit(loadPackageParam);

        if (!loadPackageParam.packageName.equals("com.tencent.mm"))
            return;

        Log.d("xposed", "we are now in wechat");

        Class<?> pluginHelper = findClass("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader);
        findAndHookMethod(pluginHelper, "onCreateOptionsMenu", Menu.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
//                param.args[0] = null;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
//
//                Log.d("xposed", "come in success");
//
//                try {
//                    Class c = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.conversation.d");
//                    Field field = c.getDeclaredField("btS");
//                    field.setAccessible(true);
//
//                    //param.thisObject 为执行该方法的对象，在这里指MainActivity
//                    ImageView logo = (ImageView) field.get(param.args[1]);
//                    ViewGroup.LayoutParams viewGroup = logo.getLayoutParams();
//                    viewGroup.height = viewGroup.height / 2;
//                    logo.setLayoutParams(viewGroup);
//
//                } catch (Exception e) {
//
//                }
            }
        });
    }

    public void testMultiDexHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("android.app.Application", loadPackageParam.classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.ap",
                        context.getClassLoader(), "c",
                        boolean.class, List.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                List list = (List) param.args[1];
                                XposedBridge.log("list:" + list);
                                for (Object object : list) {
                                    Class<?> aClass = object.getClass();
                                    aClass.getFields();
                                }
                            }
                        });
            }
        });
    }

    public void testHookRecruit(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.packageName.equals("com.libo.recruit"))
            return;

        Log.d("xposed", "we are now in recruit update");

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
                    tv2.setText("Hook Success!!update");

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
