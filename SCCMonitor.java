import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class SCCMonitor {
    
    private static String logFileBasic = "scc-0.log";
    
    private static int[] threshold = {6,6,6,6,6};
    private static int[] threadCount = {1,1,1,1,1};
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        String file_directory = "/Users/demigorgan/log";
        
        try{
            
            float optimal_score = getScore(file_directory+"/11111.log");
            String configuration = "/11111.log";
            int pool_to_increase = 2;
            
            int count = 0;
            
            while(count <= (threshold[0]*threshold[1]*threshold[2]
                            *threshold[3]*threshold[4])){
                
                if(threadCount[pool_to_increase] == 6){
                    pool_to_increase++;
                    continue;
                }
                
                threadCount[pool_to_increase-2]++;
                float cur_score = getScore(file_directory+"/"+threadCount[0]+threadCount[1]+
                                           threadCount[2]+threadCount[3]+threadCount[4]);
                
                if(cur_score < optimal_score){
                    optimal_score = cur_score;
                }
                
                else{
                    threadCount[pool_to_increase-2]--;
                    pool_to_increase++;
                }
                
                if(!(new File(file_directory+configuration).exists())){
                    
                    
                    
                }
                count++;
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
    private static float[] getAvgWaitTime(String logFile){
        float [] avgWaitTime = new float[5];
        for(int i=0;i<5;i++){
            avgWaitTime[i] = getAvgWaitTime(logFile, "pool-"+(i+2));
        }
        return avgWaitTime;
    }
    
    public static float getScore(String logFile){
        float score = 0;
        float [] weight = getRatio();
        for(int i=0;i<5;i++){
            score+=(weight[i] * getAvgWaitTime(logFile,"pool-"+(i+2)));
        }
        return score;
    }
    
    
    
    
    public static float getAvgWaitTime(String logFile,String pool){
        int count = 0;
        int total = 0;
        
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(logFile));
            while ((line = br.readLine()) != null) {
                if(line.contains("got semaphore lock") &&
                   line.contains(pool)){
                    String[] split = line.split(" ");
                    total+=Integer.parseInt(split[split.length-1]);
                    
                    count++;
                }
            }
        } catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
        
        finally {
	           if (br != null) {try {br.close();} catch (IOException e) {e.printStackTrace();}}
        }
        if(count==0)return 0;
        
        return total/count;
    }
    
    
    public static float [] getRatio(){
        float [] ratio = new float[5];
        double denominator = 0;
        
        for(int i=0;i<5;i++){
            ratio[i]=getAvgRuntime("pool-"+(i+2));
            denominator += Math.pow(ratio[i], 2.0);
        }
        denominator = Math.sqrt(denominator);
        for(int i=0;i<5;i++){
            ratio[i]/=denominator;
        }
        
        for(int i=0;i<5;i++)
            System.out.println(ratio[i]);
        
        return ratio;
    }
    
    public static float getAggregateAvgRuntime(String pool){
        HashMap<Integer, Integer> runtime = new HashMap<Integer, Integer>();
        BufferedReader br = null;
        String line;
        int count = 0;
        int total = 0;
        try{
            br = new BufferedReader(new FileReader(logFileBasic));
            while ((line = br.readLine()) != null) {
                if(line.contains(pool) && line.contains("micros") && !line.contains("semaphore")){
                    String[] split = line.split(" ");
                    int rnt = Integer.parseInt(split[split.length-4]);
                    int queryblock = Integer.parseInt(split[split.length-2]);
                    if(runtime.containsKey(queryblock)){
                        runtime.put(queryblock, runtime.get(queryblock)+ rnt);
                    }
                    else{
                        runtime.put(queryblock, rnt);
                    }
                }
            }
            
            
            Iterator it = runtime.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                total += ((int)pair.getValue());
                count++;
                it.remove(); // avoids a ConcurrentModificationException
            }
            
            
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            try{
                br.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        
        if(count == 0) return 0;
        
        return total/count;
        
    }
    
    
    public static float getAvgRuntime(String pool){
        BufferedReader br = null;
        String line;
        int total = 0;
        int count = 0; 
        
        if(pool.equals("pool-4") || pool.equals("pool-5"))
            return getAggregateAvgRuntime(pool);
        
        try{
            br = new BufferedReader(new FileReader(logFileBasic));
            while ((line = br.readLine()) != null) {
                if(line.contains(pool) && line.contains("micros") && !line.contains("semaphore")){ 			
                    String[] split = line.split(" ");	    			
                    count++;
                    int runtime = Integer.parseInt(split[split.length-2]);
                    total+=runtime;    			
                }  		
            }   	
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            try{
                br.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        if(count==0)return 0;	    
        return total/count;		
    }
    
}
