package com.vmware.eucenablement.av.ntt;

import com.vmware.eucenablement.horizontoolset.av.api.VolumeAPI;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.ExcuteResult;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Writable;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.List;

enum NTT_Operations{
    INVALID_OPERATION,
    CONNECT,
    LIST_WRITABLES,
    ENABLE_WRITABLE,
    DISABLE_WRITABLE,
    DELETE_WRITABLE,
    EXPAND_WRITABLE,
    GET_WRITABLE,
    GET_WRITABLE_SUMMARY
};

enum NTT_INPUTVOLTYPE {
    NO_NEED_VOLUMEINFO,
    SPECIFY_VOLUMEID,
    SPECIFY_USER
}

public class NttService {
    public static VolumeAPI volume;
    private final static String usage = "\nUsage: NttService -c <server ip> <domain> <user> <password> [--lw | --enaw |--disw |--delw | --getw | --getws | --expw] [--id <id> | --user <user>] [--size <size>]\n"
            + "        -c:  connect to appvolume server; Every command needs connection information\n" + "        --lw: list all the writable volumes\n" + "        --getw: get the detail information of this writable volume\n" +
            "        --getws: get the summary information of this writable volume\n" + "        --disw: disable this writable volume\n" + "        --enaw: enable this writable volume\n"
            + "        --delw: delete this writable volume\n" + "        --expw: expand this writable volume\n"
            + "        --id: the id of writable volume ids"
            + "        --user: the owner name of writable volume"
            + "        --size: the volume size to expand";


    public static void main(String[] args) {

        String ip = null, domain = null, user = null, password = null;
        NTT_Operations oper = NTT_Operations.INVALID_OPERATION;


        // Analyze arguments firstly
        try
        {
            if(args.length < 6) {
                System.out.println("The count of input parameters should be more than 6.");
            } else {
                if(args[0].compareTo("-c") != 0) {
                    System.out.println("The first parameter should be -c");
                } else {
                    ip = args[1];
                    domain = args[2];
                    user = args[3];
                    password = args[4];
                    oper = ConvertOperType(args[5]);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Caught the exception when parse input parameters");
        }

        if(oper == NTT_Operations.INVALID_OPERATION) {
            System.out.println("The input parameters are incorrect. Please check them again.");
            System.out.println(usage);
            return;
        }



        try {
            // Connect to app volume server
            PropertyConfigurator.configure("log4j.properties");
            volume = new VolumeAPI();
            volume.connect(ip, domain, user, password);
        } catch (Exception e) {
                System.out.println("can not connect to appVolumes. Exit this operation.");
                return;
        }



        try
        {
            // Get volume identifier
            ArrayList volumeIDList = GetSpecVolumeIDList(args);
            // Get volume size for expand command
            String volumeSize = GetVolumeSizeParam(args);
            // check parameters
            boolean checkedSuccess = true;
            if((oper == NTT_Operations.DELETE_WRITABLE) || (oper == NTT_Operations.DISABLE_WRITABLE) || (oper == NTT_Operations.ENABLE_WRITABLE)
                    || (oper == NTT_Operations.EXPAND_WRITABLE) || (oper == NTT_Operations.GET_WRITABLE) || (oper == NTT_Operations.GET_WRITABLE_SUMMARY))
            {
                if((volumeIDList == null) || (volumeIDList.size() == 0)) {
                    checkedSuccess = false;
                    System.out.println("Can not find the specified writable volume and please the parameters again.");
                    checkedSuccess = false;
                }
            }

            // handle commands
            if(checkedSuccess)
            {
                  // Handle the commands
                ExcuteResult result = HandleCommand(oper, volumeIDList, volumeSize);

                if(result != null) {
                    if(result.resultFlag == ExcuteResult.RES_SUCCESS) {
                        System.out.println("\n");
                        System.out.println("This operation succeeded!!");
                        System.out.println(result.message);
                    } else {
                        System.out.println("\n");
                        System.out.println("This operation failed!!");
                        System.out.println(result.message);
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Caught exeptions and please check your input parmeters.");
        }


        // dispose
        try
        {
            volume.close();
        }
        catch(Exception e)
        {
            System.out.println("close this connection failed!!" + e);
        }

    }

    /**
     * Get the operation type based on the input argument
     */
    private static NTT_Operations ConvertOperType(String value) {
        NTT_Operations oper = NTT_Operations.INVALID_OPERATION;
        switch (value) {
            case "--lw":
                {
                   oper = NTT_Operations.LIST_WRITABLES;
                }
                break;
            case "--getw":
                {
                    oper = NTT_Operations.GET_WRITABLE;
                }
                break;
            case "--getws":
                {
                    oper = NTT_Operations.GET_WRITABLE_SUMMARY;
                }
                break;
            case "--enaw":
                {
                    oper = NTT_Operations.ENABLE_WRITABLE;
                }
                break;
            case "--disw":
                {
                    oper = NTT_Operations.DISABLE_WRITABLE;
                }
                break;
            case "--delw":
                {
                    oper = NTT_Operations.DELETE_WRITABLE;
                }
                break;
            case "--expw":
                {
                    oper = NTT_Operations.EXPAND_WRITABLE;
                }
                break;
        }

        return oper;
    }


    private static String GetVolumeSizeParam(String args[]) {
        String value = null;
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.compareToIgnoreCase("--size") == 0) {
                if(i+1 < args.length) {
                    value = args[i+1];
                }
            }
        }
        return value;
    }

    private static ArrayList GetSpecVolumeIDList(String args[]) {
        ArrayList volumeIDList = null;
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.compareToIgnoreCase("--user") == 0) {
                if(i+1 < args.length) {
                    volumeIDList = GetVolumeIDByUser(args[i+1]);
                }
            } else if(arg.compareToIgnoreCase("--id") == 0) {
                if(i+1 < args.length) {
                    volumeIDList = new ArrayList();
                    volumeIDList.add(new Long(args[i+1]));
                }
            }
        }

        return volumeIDList;
    }

    private static ArrayList GetVolumeIDByUser(String userName) {
        List<Writable> Writables = volume.listWritables();
        ArrayList volumeIDs = new ArrayList();
        for(int i = 0; i < Writables.size(); i++) {
            Writable volume = Writables.get(i);
            if(volume.owner_name.compareToIgnoreCase(userName) == 0) {
                volumeIDs.add(volume.id);
            }
        }
        return volumeIDs;
    }
   private static ExcuteResult HandleCommand(NTT_Operations oper, ArrayList idList, String volumeSize) {
       ExcuteResult result = null;
       Long id = null;

       if(idList != null)
       {
           if(idList.size() > 0)
               id = new Long(idList.get(0).toString());
       }

       switch(oper)
       {
           case LIST_WRITABLES:
               List<Writable> Writables = volume.listWritables();
               result = new ExcuteResult();
               result.resultFlag = (Writables.size() > 0? ExcuteResult.RES_SUCCESS: ExcuteResult.RES_GENERAL_FAILURE);
               result.message = "The count of writable volumes is " + Writables.size() + "\n";
               for(int i = 0; i < Writables.size(); i++) {
                   Writable volume = Writables.get(i);
                   result.message = result.message + "{\"id\": " + volume.id + ", " + "\"owner_name\": " + volume.owner_name + ", " + "\"owner_type\": " + volume.owner_type + "}\n";
               }

               break;
           case ENABLE_WRITABLE:
               {
                   result = volume.enableWritableVolume(id);
               }
               break;
           case DISABLE_WRITABLE:
               {
                   result = volume.disableWritableVolume(id);
               }
               break;
           case DELETE_WRITABLE:
               {
                   result = volume.deleteWritableVolume(id);
               }
               break;
           case EXPAND_WRITABLE:
               {
                   result = volume.expandWritableVolume(id, new Long(volumeSize));
               }
               break;
           case GET_WRITABLE:
               {
                   result = new ExcuteResult();
                   String jsonResult = volume.getWritable4Json(id);
                   result.resultFlag = (jsonResult.length()> 50? ExcuteResult.RES_SUCCESS: ExcuteResult.RES_GENERAL_FAILURE);
                   result.message = jsonResult;
               }
               break;
           case GET_WRITABLE_SUMMARY:
           {
               Writable vol = volume.getWritable(id);
               result = new ExcuteResult();
               if(vol == null) {
                   result.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                   result.message = "Cannot get the information for this writable volume.";
               } else {
                   result.resultFlag = ExcuteResult.RES_SUCCESS;
                   result.message = "Information of this volume is as below:\n";
                   result.message = result.message + "\"id\": " + vol.id + ", " + "\"name\": " + vol.name + ", \"owner_name\": " + vol.owner_name + ", \"owner_type\": " + vol.owner_type + "\n";
                   result.message = result.message + "\"status\": " + vol.status + ", \"file_location\": " + vol.file_location + "\n";
                   result.message = result.message + "\"free_mb\": " + vol.free_mb + ", \"total_mb\": " + vol.total_mb + "\n";
               }
           }
       }


       return result;
   }

}
