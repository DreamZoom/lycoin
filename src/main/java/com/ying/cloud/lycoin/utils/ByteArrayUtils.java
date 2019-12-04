package com.ying.cloud.lycoin.utils;

import com.ying.cloud.lycoin.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteArrayUtils {
    public static <T> T decode(byte[] bytes){
        try{

            ByteArrayInputStream inputStream =new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            T message = (T)ois.readObject();
            ois.close();
            return message;

        }
        catch (Exception err){
            System.out.println(err.getMessage());
        }
        return null;
    }

    public static byte[] encode(Object obj){
        try{
            ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(obj);
            return outputStream.toByteArray();
        }
        catch (Exception error){
            System.err.println(error.getMessage());
        }
        return null;
    }
}
