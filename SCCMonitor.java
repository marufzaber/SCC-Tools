import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SCCMonitor {
	
	private static String logFileBasic = "scc-0.log";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BufferedWriter bw =  null;
		
		try{
			File fout = new File("scores.csv");		
	    	FileOutputStream fos = new FileOutputStream(fout);
			 bw = new BufferedWriter(new OutputStreamWriter(fos));
		    
	        String str = args[0]+"-"+args[1]+"-"+args[2]+"-"+args[3]+"-"+args[4];
	        str += ',';
	        str += getScore("scc.log");
	        bw.write(str);
	        bw.newLine();
	        
	        System.out.println("SCORE : "+str);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			try{
				bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
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
	 	    			// System.out.println(str + "     :     "+i);
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
	
	
	public static float getAvgRuntime(String pool){
		BufferedReader br = null;
	    String line;
	    int total = 0;
	    int count = 0;   	      
	    try{
	    	br = new BufferedReader(new FileReader(logFileBasic));
	    	while ((line = br.readLine()) != null) {
	    		if(line.contains(pool) && line.contains("micros")
	    			&& !line.contains("semaphore")){ 			
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
