package seu.qz.qzapp.activity.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import seu.qz.qzapp.R;
import seu.qz.qzapp.activity.CustomerInfoSettingActivity;
import seu.qz.qzapp.activity.operation.CustomerInfoSettingOperation;
import seu.qz.qzapp.database.LitePalUtils;
import seu.qz.qzapp.entity.AppCustomer;

public class CustomerInfoSettingViewModel extends ViewModel {

    private CustomerInfoSettingOperation operation = new CustomerInfoSettingOperation();

    AppCustomer mainCustomer;


    public AppCustomer getMainCustomer() {
        return mainCustomer;
    }

    public void setMainCustomer(AppCustomer mainCustomer) {
        this.mainCustomer = mainCustomer;
    }

    public void updateNewCustomer(final AppCustomer new_customer, final ProgressBar progressbar, final Context context) {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case 0:
                        if(progressbar != null){
                            progressbar.setVisibility(View.GONE);
                        }
                        AlertDialog.Builder dialog_netError = new AlertDialog.Builder(context);
                        dialog_netError.setTitle(R.string.order_error);
                        dialog_netError.setMessage(R.string.order_netError_content);
                        dialog_netError.setCancelable(false);
                        dialog_netError.setPositiveButton(R.string.order_netError_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                context.startActivity(intent);
                            }
                        });
                        dialog_netError.setNegativeButton(R.string.order_netError_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog_netError.show();
                        break;
                    //返回为空，稍后再试
                    case 1:
                        if(progressbar != null){
                            progressbar.setVisibility(View.GONE);
                        }
                        AlertDialog.Builder dialog_empty = new AlertDialog.Builder(context);
                        dialog_empty.setTitle(R.string.passwordsetting_updateError_empty_title);
                        String cause = (String) msg.obj;
                        if(cause.equals("Same Email")){
                            dialog_empty.setMessage("Email已被注册！");
                        }else if(cause.equals("Same phoneNumber")){
                            dialog_empty.setMessage("电话号码已被注册！");
                        }else {
                            dialog_empty.setMessage(R.string.passwordsetting_updateError_empty_message);
                        }
                        dialog_empty.setCancelable(false);
                        dialog_empty.setPositiveButton(R.string.order_netError_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog_empty.show();
                        break;
                    //修改成功
                    case 2:
                        if(progressbar != null){
                            progressbar.setVisibility(View.GONE);
                        }
                        LitePalUtils.saveSingleCustomer(new_customer);
                        AlertDialog.Builder dialog_success = new AlertDialog.Builder(context);
                        dialog_success.setTitle(R.string.customerinfosetting_updateSuccess_title);
                        dialog_success.setMessage(R.string.customerinfosetting_updateSuccess_message);
                        dialog_success.setCancelable(false);
                        dialog_success.setPositiveButton(R.string.customerinfosetting_updateSuccess_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Activity activity = (Activity) context;
                                activity.finish();
                            }
                        });
                        dialog_success.show();
                        break;
                    case 3:
                        //以通知的方法告知查询时出现错误
                        AlertDialog.Builder dialog_error = new AlertDialog.Builder(context);
                        dialog_error.setTitle(R.string.order_error);
                        dialog_error.setMessage(R.string.order_unknownError);
                        dialog_error.setCancelable(false);
                        dialog_error.setPositiveButton(R.string.order_netError_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog_error.show();
                        break;
                    default:break;
                }
            }
        };
        operation.updatePassword(new_customer, handler, context);
    }
}
