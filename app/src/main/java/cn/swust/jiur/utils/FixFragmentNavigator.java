package cn.swust.jiur.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import org.jsoup.select.Evaluator;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Map;

@Navigator.Name("fixFragment")
public class FixFragmentNavigator extends FragmentNavigator {
    private final String TAG = "FixFragmentNavigator";
    private Context mContext;
    private FragmentManager mManager;
    private int mContainerId;
    public FixFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        this.mContext = context;
        this.mManager = manager;
        this.mContainerId = containerId;
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        if (mManager.isStateSaved()){
            Log.i(TAG,"Ignore navigate()");
            return null;
        }
        String className = destination.getClassName();
        if(className.charAt(0) == '.'){
            className = mContext.getPackageName() + className;
        }

        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        Integer enter = -1,exitAnim=-1,popEnterAnim=-1,popExitAnim=-1;
        if(navOptions != null){
             enter = (Integer.valueOf(navOptions.getEnterAnim())==null)?navOptions.getEnterAnim():-1;
             exitAnim = (Integer.valueOf(navOptions.getExitAnim())==null)?navOptions.getEnterAnim():-1;
             popEnterAnim = (Integer.valueOf(navOptions.getPopEnterAnim())==null)?navOptions.getEnterAnim():-1;

             popExitAnim = (Integer.valueOf(navOptions.getPopExitAnim())==null)?navOptions.getEnterAnim():-1;
        }

        if(enter !=-1 || exitAnim != -1| popEnterAnim != -1 || popExitAnim != -1){
            enter = enter == -1 ? 0 : enter;
            exitAnim = exitAnim == -1 ? 0 : exitAnim;
            popEnterAnim = popEnterAnim == -1 ? 0 : popEnterAnim;
            popExitAnim = popExitAnim == -1 ? 0 : popExitAnim;
            fragmentTransaction.setCustomAnimations(enter,exitAnim,popEnterAnim,popExitAnim);
        }

        Fragment fragment = mManager.getPrimaryNavigationFragment();
        if(fragment!= null){
            fragmentTransaction.hide(fragment);
            fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }
        String tag = String.valueOf(destination.getId());
        Fragment fragmentTag = mManager.findFragmentByTag(tag);
        if(fragmentTag != null){
            fragmentTransaction.show(fragmentTag);
            fragmentTransaction.setMaxLifecycle(fragmentTag, Lifecycle.State.RESUMED);
        } else {
            fragmentTag = instantiateFragment(mContext,mManager,className,args);
            fragmentTag.setArguments(args);
            fragmentTransaction.add(mContainerId,fragmentTag,tag);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTag);
        
        @IdRes int id = destination.getId();
        ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
        /**
         * 反射获取返回栈
         */
        try {
            Field field = FragmentNavigator.class.getDeclaredField("mBackStack");
            field.setAccessible(true);
            
            mBackStack = (ArrayDeque<Integer>) field.get(this);
            boolean stackEmpty = mBackStack.isEmpty();
            boolean isSingleTopReplacement = navOptions != null && !stackEmpty && navOptions.shouldLaunchSingleTop()
                    && mBackStack.peekLast() == id;
            boolean isAdded;
            if(stackEmpty){
                isAdded = true;
            } else if (isSingleTopReplacement) {
                if(mBackStack.size() > 1){
                    mManager.popBackStack(zygoteBackStackName(mBackStack.size(),mBackStack.peekLast()),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction.addToBackStack(zygoteBackStackName(mBackStack.size(),id));
                }
                isAdded = false;
            } else {
                fragmentTransaction.addToBackStack(zygoteBackStackName(mBackStack.size()+1,id));
                isAdded = true;
            }

            if(navigatorExtras instanceof Extras){
                Extras extras = (Extras) navigatorExtras;
                for (Map.Entry<View, String> viewStringEntry : extras.getSharedElements().entrySet()) {
                    fragmentTransaction.addSharedElement(viewStringEntry.getKey(),viewStringEntry.getValue());
                }
            }
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
            if(isAdded){
                mBackStack.add(id);
                return destination;
            } else {
                return null;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private String zygoteBackStackName(int size, Integer peekLast) {
        return size + " - " + peekLast;
    }
}
