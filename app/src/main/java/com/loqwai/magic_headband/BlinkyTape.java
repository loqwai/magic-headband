package com.loqwai.magic_headband;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by redaphid on 2/18/15.
 */
public class BlinkyTape {
    private UsbManager manager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbSerialDriver driver;
    private UsbSerialPort port;

    public BlinkyTape(UsbManager manager, UsbDevice device) {
        this.manager = manager;
        this.device = device;
        this.driver = new CdcAcmSerialDriver(device);
        port = driver.getPorts().get(0);
    }

    public boolean connect() throws IOException {
        connection = manager.openDevice(device);
        if(connection == null) {
            return false;
        }
        port.open(connection);
        port.setParameters(57600, 8, 1, 0);
        byte[] bytes = new byte[64 * 3];
        for( int i = 0; i < bytes.length - 1; i++)
            bytes[i] = (byte)254;

        bytes[bytes.length - 1] = (byte)255;
        port.write(bytes, 500);
        return true;
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
