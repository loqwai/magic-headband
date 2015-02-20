package com.loqwai.magic_headband;

import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by redaphid on 2/18/15.
 */
public class BlinkyTape {
    public static int LED_COUNT = 60;
    private static final int TIMEOUT = 500;
    private UsbManager manager;
    private UsbDevice device;
    private UsbSerialPort port;
    private boolean readyToGo;

    public BlinkyTape(UsbManager manager, UsbDevice device) {
        this.manager = manager;
        this.device = device;
        UsbSerialDriver driver = new CdcAcmSerialDriver(device);
        port = driver.getPorts().get(0);
    }

    public boolean connect() throws IOException {
        readyToGo = false;
        UsbDeviceConnection connection = manager.openDevice(device);
        if(connection == null) {
            return false;
        }
        port.open(connection);
        port.setParameters(57600, 8, 1, 0);

        readyToGo = true;
        return true;
    }

    public boolean render (int color) {
        List<Integer> colors = new ArrayList<>(60);
        for(int i = 0; i < 60; i++){
            colors.add(color);
        }

        return render(colors);
    }

    public boolean render(Collection<Integer> colors) {
        byte[] bytes = new byte[colors.size() * 3 + 2];
        int i = 1;
        for(Integer color : colors) {
           bytes[i++] = (byte)Color.red(color);
            bytes[i++] = (byte)Color.green(color);
            bytes[i++] = (byte)Color.blue(color);
        }
        for(i = 1; i < bytes.length; i++) {
            if(bytes[i] == -1)
                bytes[i] = (byte)254;
        }
        bytes[0] = (byte)255;
        bytes[bytes.length - 1] = (byte)255;

        try {
            port.write(bytes, TIMEOUT);
            port.write(bytes, TIMEOUT);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static BlinkyTape findBlinkyTape(UsbManager manager) {
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        for(UsbDevice device : devices.values()) {
            if(device.getProductName().trim().indexOf("BlinkyTape") == 0){
                return new BlinkyTape(manager, device);
            }
        }

        return null;
    }

    public UsbDevice getDevice() {
        return device;
    }
}
