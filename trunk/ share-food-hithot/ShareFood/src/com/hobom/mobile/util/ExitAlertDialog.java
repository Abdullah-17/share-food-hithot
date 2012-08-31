package com.hobom.mobile.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class ExitAlertDialog {

	private Activity activity;

	public ExitAlertDialog(Activity activity) {
		this.activity = activity;
	}

	/**
	 * �����Ի���
	 */
	public void createDialog() {

		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setTitle("�˳�")
				.setMessage("ȷ��Ҫ�˳���")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ActivityStackManager.getInstance().destroySelf();
						//activity.finish();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);

						ActivityManager actMgr = (ActivityManager) activity
								.getSystemService("activity");
						actMgr.restartPackage(activity.getPackageName());
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		alertDialog.show();

	}

	/**
	 * �����Ի���
	 */
	public void createDialog(String message) {

		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setTitle("�˳�").setMessage(message)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		alertDialog.show();

	}

	public void createForceExitDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setTitle("�˳�")
				.setMessage("ȷ��Ҫ�˳���")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ActivityStackManager.getInstance().destroySelf();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);

						ActivityManager actMgr = (ActivityManager) activity
								.getSystemService("activity");
						actMgr.restartPackage(activity.getPackageName());
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		alertDialog.show();
	}

	
}
