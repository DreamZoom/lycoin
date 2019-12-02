package com.ying.cloud.lycoin.store;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StoreUtils {
    public static void save(Object o){

        String key = o.getClass().getName();
        try{
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("./data/"+key+".dat"));
            outputStream.writeObject(o);
            outputStream.close();
        }catch (Exception error){
            System.out.println(error.getMessage());
        }

    }

    public static  <T> T load(Class<T> cls){
        String key = cls.getName();
        try{
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("./data/"+key+".dat"));
            T obj =(T)inputStream.readObject();
            inputStream.close();
            return obj;
        }catch (Exception error){
            System.out.println(error.getMessage());
        }
        return null;
    }
}
