package com.hdl.udpsender;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hdl.elog.ELog;
import com.hdl.udpsenderlib.UDPResult;
import com.hdl.udpsenderlib.UDPResultCallback;
import com.hdl.udpsenderlib.UDPSender;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView tvReuslt;
    private ProgressDialog mProgressDialog;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvReuslt = (TextView) findViewById(R.id.tv_result);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("扫描中，请稍后");
//        UDPReceiver.getInstance().with(this)
//                .setPort(9988)
//                .receive(new UDPResultCallback() {
//                    @Override
//                    public void onNext(UDPResult result) {
//
//                    }
//                });

    }

    /**
     * 开始扫描
     *
     * @param view
     */
    public void onStart(View view) {
//        shakeSimple();//简单调用的例子
        final ShakeData data = new ShakeData();
        data.setCmd(ShakeData.Cmd.CMD_SHAKE_DEVICE);
        UDPSender.getInstance()
                .setInstructions(ShakeData.getShakeDataCastByteArray(data))//设置发送的指令[必须，不可为空]
                .setReceiveTimeOut(10 * 1000)//设置接收超时时间[可不写，默认为8s]--超过10s没有接收到设备就视为无设备了就可以停止当前任务了
                .setTargetPort(ShakeData.Cmd.CMD_SHAKE_DEVICE_DEFAULT_PORT)//设置发送的端口[可不写，默认为8899端口]
                .setLocalReceivePort(ShakeData.Cmd.CMD_SHAKE_DEVICE_DEFAULT_PORT_RECEIVE)//设置本机接收的端口[可不写，默认为8899端口]
                .schedule(2, 3000)//执行2次，间隔三秒执行
                .start(new UDPResultCallback() {
                    /**
                     * 请求开始的时候回调
                     */
                    @Override
                    public void onStart() {
                        count = 1;
                        tvReuslt.setText("");
                        mProgressDialog.show();
                    }

                    /**
                     * 每拿到一个结果的时候就回调
                     *
                     * @param result 请求的结果
                     */
                    @Override
                    public void onNext(UDPResult result) {
                        ELog.hdl("" + result);
                        ShakeData dataResult = ShakeData.getShakeDataResult(result.getResultData());
                        if (dataResult.getCmd() == ShakeData.Cmd.CMD_RECEIVE_MESSAGE_HEADER_CMDID) {
                            int id = dataResult.getId();
                            String pwd = dataResult.getFlag() == 1 ? "有密码" : "无密码";
                            tvReuslt.append((count++) + ")\t ip = " + result.getIp() + "\t\t\tid = " + id + "\t\t\t" + pwd + "\n\n");
                            ELog.hdl("result = " + dataResult.toString());
                        }
                    }

                    /**
                     * 请求结束的时候回调
                     */
                    @Override
                    public void onCompleted() {
                        mProgressDialog.dismiss();
                        Log.e(TAG, "onCompleted");
                    }

                    /**
                     * 当发生错误的时候回调
                     *
                     * @param throwable
                     */
                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: " + throwable.getMessage());
                        Toast.makeText(MainActivity.this, "任务已在执行中", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 发送UDP广播最简答的一个例子，（可以不用设置超时时间，端口号，不用重写onStart，onError，onCompleted方法即可拿到结果）
     */
    private void shakeSimple() {
//        count = 1;
//        tvReuslt.setText("");
//        ShakeData data = new ShakeData();
//        data.setCmd(ShakeData.Cmd.CMD_SHAKE_DEVICE);
//
//        UDPSender.getInstance()
//                .setInstructions(ShakeData.getShakeDataCastByteArray(data))
//                .start(new UDPResultCallback() {
//                    /**
//                     * 每拿到一个结果的时候就回调
//                     *
//                     * @param result 请求的结果
//                     */
//                    @Override
//                    public void onNext(UDPResult result) {
//                        ShakeData dataResult = ShakeData.getShakeDataResult(result.getResultData());
//                        int id = dataResult.getId();
//                        String pwd = dataResult.getFlag() == 1 ? "有密码" : "无密码";
//                        tvReuslt.append((count++) + ")\t ip = " + result.getIp() + "\t\t\tid = " + id + "\t\t\t" + pwd + "\n\n");
//                    }
//                });
    }

    /**
     * 停止任务
     *
     * @param view
     */
    public void onStop(View view) {
        UDPSender.getInstance().stop();
    }

    public void onTest(View view) {
        UDPSender.getInstance()
                .setInstructions(new byte[]{0})
                .setTargetPort(8890)
                .setLocalReceivePort(8787)
                .start(new UDPResultCallback() {
                    @Override
                    public void onNext(UDPResult result) {
                        ELog.hdl("result" + result);
                    }
                });
    }

    public void onReceive(View view) {
//        UDPReceiver.getInstance().with(this)
//                .setPort(9988)
//                .receive(new UDPResultCallback() {
//                    @Override
//                    public void onNext(UDPResult result) {
//                        ELog.hdl(""+result);
//                    }
//                });
//        ELog.hdl("开始接收了");
//        UDPThread udpThread = new UDPThread();
        ShakeData shakeData = new ShakeData();
        shakeData.setCmd(1);
        send(shakeData);
//        udpThread.setInstructions(ShakeData.getShakeDataCastByteArray(shakeData));
//        udpThread.start();
//        start(shakeData);
//        UDPSender.getInstance()
//                .setLocalReceivePort(9988)
//                .start(new UDPResultCallback() {
//                    @Override
//                    public void onStart() {
//                        ELog.hdl("开始了");
//                    }
//
//                    @Override
//                    public void onNext(UDPResult result) {
//                        ELog.hdl("" + result);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        ELog.hdl("出错了" + throwable);
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        ELog.hdl("完成了");
//                    }
//                });

    }

    private void send(ShakeData shakeData) {
        UDPSender.getInstance()
                .setReceiveTimeOut(10 * 1000)
                .setTargetPort(8899)
                .setLocalReceivePort(8899)
                .setInstructions(ShakeData.getShakeDataCastByteArray(shakeData))
                .schedule(2, 3000)
                .start(new UDPResultCallback() {
                    @Override
                    public void onStart() {
                        ELog.hdl("开始了");
                    }

                    @Override
                    public void onNext(UDPResult result) {
                        ELog.hdl("" + result);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ELog.hdl("出错了" + throwable);
                    }

                    @Override
                    public void onCompleted() {
                        ELog.hdl("完成了");
                    }
                });
    }

    /**
     * 设备体检
     *
     * @param view
     */
    public void onDeviceTest(View view) {
        byte data[] = {50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, -64, -88, 0, 127, 57, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, -104, 92, 23, -90, 25, 0, 0};
        UDPSender.getInstance()
                .setTargetIp("192.168.0.126")
                .setTargetPort(8899)
                .setReceiveTimeOut(20 * 1000)
                .setLocalReceivePort(8899)
                .setInstructions(data)
                .start(new UDPResultCallback() {
                    /**
                     * 请求开始的时候回调
                     */
                    @Override
                    public void onStart() {
                        super.onStart();
                        ELog.hdl("开始体检了");
                    }

                    /**
                     * 每拿到一个结果的时候就回调
                     *
                     * @param result 请求的结果
                     */
                    @Override
                    public void onNext(UDPResult result) {
                        ELog.hdl("开始拿到结果了");
                        ELog.hdl("" + result);
                    }

                    /**
                     * 请求结束的时候回调
                     */
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        ELog.hdl("体检完成了");
                    }

                    /**
                     * 当发生错误的时候回调
                     *
                     * @param throwable 错误信息
                     */
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        ELog.hdl("发生错误了 " + throwable);
                    }
                });
    }
    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = ((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF);
        return value;
    }
    public int bytes2ToInt(byte[] src, int offset) {
        int value;
        value = (src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8);
        return value;
    }
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24);
        return value;
    }
    public void onUpdateId(View view) {
        ELog.hdl("instructions ----> " + Arrays.toString(getInstructions(2028252)));
        ELog.hdl("contactId ----> 2028252");
        ELog.hdl("id ----> 192.168.0.119");
        UDPSender.getInstance()
                .setInstructions(getInstructions(2028252))
                .setTargetPort(8899)
                .setLocalReceivePort(8899)
                .setReceiveTimeOut(30 * 1000)
                .setTargetIp("192.168.0.109")
                .start(new UDPResultCallback() {
                    @Override
                    public void onStart() {
                        ELog.hdl("开始");
                        tvReuslt.append("\n开始\n\n");
                    }

                    @Override
                    public void onNext(UDPResult result) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.put(result.getResultData());
//                        int cmd = buffer.getInt(0);
                        int cmd = bytesToInt(result.getResultData(), 0);
                        int isSuccess = bytesToInt(result.getResultData(), 4);
                        int oldId = bytesToInt(result.getResultData(), 8);
                        int newId = bytesToInt(result.getResultData(), 12);
                        buffer.clear();
                        tvReuslt.append(Arrays.toString(Arrays.copyOf(result.getResultData(), 16)) + "\n\n");
                        ELog.hdl("" + Arrays.toString(result.getResultData()));
                        ELog.hdl("-----> cmd = " + cmd + "\t isSuccess = " + isSuccess + "\toldId = " + oldId + "\tnewId = " + newId);
                        tvReuslt.append("-----> cmd = " + cmd + "\t isSuccess = " + isSuccess + "\toldId = " + oldId + "\tnewId = " + newId + "\n\n");

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ELog.hdl("" + throwable);
                        tvReuslt.append("出错了" + throwable + "\n\n");
                    }

                    @Override
                    public void onCompleted() {
                        ELog.hdl("完成");
                        tvReuslt.append("完成\n\n");
                    }
                });
    }

    public byte[] getInstructions(int contactId) {
        byte[] bytes;
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(intToByte4(53));
        buffer.put(intToByte4(contactId));
        bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (i & 0xFF);
        targets[1] = (byte) (i >> 8 & 0xFF);
        targets[2] = (byte) (i >> 16 & 0xFF);
        targets[3] = (byte) (i >> 24 & 0xFF);
        return targets;
    }
}
