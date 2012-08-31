package com.hobom.mobile.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

import com.hobom.mobile.ui.AnalysisActivity;
import com.hobom.mobile.ui.CameraActivity;
import com.hobom.mobile.ui.FoodListActivity;
import com.hobom.mobile.ui.FoodMapActivity;
import com.hobom.mobile.ui.MoreGroup;

/**
 * activity栈管理
 * 
 * @author zhangmingxun
 * 
 */
public class ActivityStackManager {

	private final static String TAG = "StackManager";
	public static ActivityStackManager instance;
	private LinkedList<Activity> activityStack;
	private ArrayList<String> magicActivity;

	private ActivityStackManager() {
		activityStack = new LinkedList<Activity>();
		magicActivity = new ArrayList<String>() {
			{

				add(FoodListActivity.class.getName());
				add(FoodMapActivity.class.getName());
				add(MoreGroup.class.getName());
				add(CameraActivity.class.getName());
				add(AnalysisActivity.class.getName());

			}
		};

	}

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static ActivityStackManager getInstance() {
		if (instance == null)
			instance = new ActivityStackManager();
		return instance;
	}

	/**
	 * 增加一个activity
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		System.out.println("add activity:"
				+ activity.getComponentName().getClassName());

		activityStack.offer(activity);
	}

	/**
	 * 移除一个activity
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		System.out.println("remove activity:"
				+ activity.getComponentName().getClassName());
		if (activityStack.contains(activity)) {
			System.out.println("remove succeed");
			activityStack.remove(activity);

		}
	}

	private boolean isInMagic() {

		for (Activity activity : activityStack) {
			System.out.println("the activity is:"
					+ activity.getComponentName().getClassName());
			if (!magicActivity.contains(activity.getComponentName()
					.getClassName()))
				return false;
		}
		return true;

	}

	private boolean canExit() {
		System.out.println("the activitystack size is:" + activityStack.size());

		if (activityStack.size() <= 1) {
			return true;
		} else if (isInMagic()) {
			System.out.println("is in magic");
			return true;
		} else
			return false;

	}

	/**
	 * 按返回键时调用
	 * 
	 * @param activity
	 */
	public void back(Activity activity) {

		if (canExit()) {
			// 保存比例尺
			System.out.println("can exit");
			// PreferenceUtil.setSettingInt(Constant.LASTLAT,
			// (int)(MapActivity.latitude*3600*1000));
			// PreferenceUtil.setSettingInt(Constant.LASTLON,
			// (int)(MapActivity.longitude*3600*1000));
			new ExitAlertDialog(activity).createDialog();
		} else {
			System.out.println("just remove and finish");
			if (magicActivity.contains(activity.getComponentName()
					.getClassName())) {
				System.out.println("in magic remove");
				int index = activityStack.indexOf(activity);
				System.out.println("the index is:"+index);
				List<Activity> copy = new LinkedList<Activity>();
				copy.addAll(activityStack);
				Activity current = null;
				for (int i = activityStack.size() - 1; i > index; --i) {
					current = copy.get(i);
					System.out.println("the current name is:"+current.getComponentName().getClassName());
					removeActivity(current);
					if (!current.isFinishing())
						current.finish();
				}
				
				if(canExit()){
					new ExitAlertDialog(activity).createDialog();
				}else{
					removeActivity(activity);
					if (!activity.isFinishing())
						activity.finish();
				}
			} else {

				removeActivity(activity);
				if (!activity.isFinishing())
					activity.finish();
			}

		}
	}

	/**
	 * 销毁栈中全部的activity
	 */
	public void destroySelf() {
		for (Activity activity : activityStack) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}

}
