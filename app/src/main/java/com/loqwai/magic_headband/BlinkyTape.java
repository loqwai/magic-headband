package com.loqwai.magic_headband;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.util.HashMap;
import java.util.List;

/**
 * Created by redaphid on 2/18/15.
 */
public class BlinkyTape {
    private UsbManager manager;
    private UsbDevice device;

    public BlinkyTape(UsbManager manager, UsbDevice device) {
        this.manager = manager;
        this.device = device;
    }

    public void connect () {
        UsbDeviceConnection connection = manager.openDevice(device);
        System.out.println(connection.getSerial());
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
}
