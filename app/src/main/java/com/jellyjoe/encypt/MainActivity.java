package com.jellyjoe.encypt;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jellyjoe.encypt.utils.MyExcutorManager;
import com.jellyjoe.encypt.utils.SecurityUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mPassWord;
    private EditText mTargetText;
    private Button mBtnCopy;
    private Button mBtnClear;
    private Button mBtnEncrypt;
    private Button mBtndecrypt;

    private ClipboardManager mClipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (mClipboardManager == null) {
            return;
        }

        if (mClipboardManager.hasPrimaryClip()) {
            ClipData data = mClipboardManager.getPrimaryClip();
            String content = "";
            int count = data.getItemCount();
            for (int i = 0; i < count; i++) {
                ClipData.Item item = data.getItemAt(i);
                CharSequence str = item.coerceToText(this);
                content += str;
            }

            showDialog(content);
        }

    }

    private void initView() {
        mPassWord = (EditText) findViewById(R.id.password);
        mTargetText = (EditText) findViewById(R.id.target);
        mBtnCopy = (Button) findViewById(R.id.copy);
        mBtnClear = (Button) findViewById(R.id.clear);
        mBtnEncrypt = (Button) findViewById(R.id.btnEncrypt);
        mBtndecrypt = (Button) findViewById(R.id.btnDecrypt);

        mBtnCopy.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mBtnEncrypt.setOnClickListener(this);
        mBtndecrypt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy:
                copy();
                break;
            case R.id.clear:
                clear();
                break;
            case R.id.btnEncrypt:
                doAction(true);
                break;
            case R.id.btnDecrypt:
                doAction(false);
                break;
            default:break;
        }
    }

    private void copy() {
        if(!TextUtils.isEmpty(mTargetText.getText().toString())) {
            ClipData data = ClipData.newPlainText("crypt_data" , mTargetText.getText());
            mClipboardManager.setPrimaryClip(data);
            Toast.makeText(MainActivity.this, "已复制", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "没有可已复制的内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void clear() {
        mTargetText.setText("");
    }

    private ActionCallback mCallback = new ActionCallback() {
        @Override
        public void onSuccess(final String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTargetText.setText(result);
                }
            });
        }

        @Override
        public void onFail(final String error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    private void doAction(final boolean isEncrypt) {
        if (checkContent()) {
            Toast.makeText(MainActivity.this, isEncrypt ?"加密中..." : "解密中...", Toast.LENGTH_SHORT).show();

            MyExcutorManager.getInstance().singleThreadExecute(new Runnable() {
                @Override
                public void run() {
                    if (isEncrypt) {
                        SecurityUtils.enrypt(mPassWord.getText().toString(), mTargetText.getText().toString(), mCallback);
                    } else {
                        SecurityUtils.decrypt(mPassWord.getText().toString(), mTargetText.getText().toString(), mCallback);
                    }
                }
            });

        }
    }

    private boolean checkContent() {
        if (TextUtils.isEmpty(mPassWord.getText().toString())) {
            Toast.makeText(this, "请输入密码!!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(mTargetText.getText().toString())) {
            Toast.makeText(this, "请输入内容!!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showDialog(final String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否粘贴剪贴板中的内容?");
        builder.setMessage(content);
        builder.setPositiveButton("粘贴", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTargetText.setText(content);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

}
