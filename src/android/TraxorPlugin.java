package com.traxor.plugins;

import java.util.*;
import java.io.*;

import android.util.Log;
import android.net.*;
import android.util.*;

import org.apache.cordova.*;
import org.apache.cordova.contacts.*;
import org.json.*;

public class TraxorPlugin extends CordovaPlugin
{

    private static final String TAG = "TraxorPlugin";
    
    @Override
    public boolean execute (final String action,
                            final JSONArray args,
                            final CallbackContext callbackContext)
    throws JSONException
    {
        
        if ("getContactsByIds".equals (action))
        {
        
            final JSONArray ids = args.getJSONArray (0);
            final JSONArray fields = args.getJSONArray (1);
            
            final TraxorPlugin _this = this;
            
            if (ids == null)
            {
                
                callbackContext.error ("No ids provided");
                
            }
                        
            this.cordova.getThreadPool ().execute (new Runnable ()
            {
           
                public void run ()
                {
                
                    ContactAccessor acc = new ContactAccessorSdk5 (_this.cordova);

                    JSONArray ret = new JSONArray ();
                    JSONObject c = null;
                    
                    for (int i = 0; i < ids.length (); i++)
                    {
                        
                        String id = null;
                        
                        try
                        {
                            
                            id = ids.getString (i);
                            
                        } catch (JSONException e) {
                        
                            Log.e (TAG, "Unable to get id for index: " + i, e);
                            
                            continue;
                            
                        }
                        
                        if (id == null)
                        {
                            
                            continue;
                            
                        }
                        
                        try
                        {
                        
                            c = acc.getContactById (id, fields);
                            
                        } catch (JSONException e) {
                            
                            Log.e (TAG, "Unable to get contact with id: " + id, e);
                            
                            continue;
                            
                        }
                        
                        if (c == null)
                        {
                            
                            continue;
                            
                        }
                        /*
                        try
                        {                                            
                        
                            JSONArray photos = c.optJSONArray ("photos");
                                                    
                            if (photos != null)
                            {
                                                            
                                JSONObject photo = photos.optJSONObject (0);
                                
                                if (photo != null)
                                {
                                
                                    String type = photo.getString ("type");
                                    String value = photo.getString ("value");
                                    
                                    if ((value != null)
                                        &&
                                        (type.equals ("url"))
                                       )
                                    {
                                        
                                        Uri uri = Uri.parse (value);
                                        
                                        InputStream in = new BufferedInputStream (_this.cordova.getActivity().getApplicationContext().getContentResolver ().openInputStream (uri));
                                        
                                        ByteArrayOutputStream out = new ByteArrayOutputStream ();
                                        
                                        int nRead;
                                        byte[] data = new byte[16384];
                                        
                                        while ((nRead = in.read (data, 0, data.length)) != -1)
                                        {
                                            
                                          out.write (data, 0, nRead);
                                          
                                        }
                                        
                                        byte[] photobytes = out.toByteArray ();
                                        
                                        String s = "data:image/png;base64," + Base64.encodeToString (photobytes,
                                                                                         Base64.DEFAULT);
                                        
                                        photo.put ("value", s);
                                        photo.put ("type", "base64");
                                        
                                    }
                                    
                                }
                                
                            }
                        
                        } catch (Exception e) {
                            
                            Log.e (TAG,
                                   "Unable to get photo for: " + id,
                                   e);
                            
                            continue;
                            
                        }
                        */
                        ret.put (c);
                        
                    }
                    
                    callbackContext.success (ret);
                
                }
            
            });
         
            return true;
            
        }
        
        return false;
        
    }

}