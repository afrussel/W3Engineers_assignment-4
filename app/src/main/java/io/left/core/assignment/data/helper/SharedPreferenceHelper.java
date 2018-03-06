package io.left.core.assignment.data.helper;

import android.content.Context;

import io.left.core.util.lib.shared_preference.SharedPreferencePrivateMode;


/**
 * Created by Tariqul on 12/28/2017.
 *
 * In this class we save some data in shared preference.
 * Like patient info
 * User mode
 */

public class SharedPreferenceHelper extends SharedPreferencePrivateMode {
    public SharedPreferenceHelper(Context mContext) {
        super(mContext);
    }

    private static SharedPreferenceHelper instance;

    synchronized public static SharedPreferenceHelper onInstance(Context context){

        if(instance==null){
            instance = new SharedPreferenceHelper(context);
        }
        return instance;
    }

    private static String FirstName = "first_name";
    private static String LastName = "last_name";



    public  String getFirstName() {
        return readString(FirstName,"");
    }

    public  void setFirstName(String firstName) {
        writeString(FirstName,firstName);
    }

    public  String getLastName() {
        return readString(LastName,"");
    }

    public  void setLastName(String lastName) {
        writeString(LastName,lastName);
    }
}
