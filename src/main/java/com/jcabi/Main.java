/**
 * Copyright (c) 2013-2015, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.response.JsonResponse;
import com.gitoperations.CloneRemoteRepository;
import com.opencsv.CSVWriter;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.json.*;

/**
 * Search repositories.
 *
 * @author Foyzul Hassan
 * @version $Id: 1 $
 * @since 0.1
 */

public final class Main {

    /**
     * Main entry point.
     * @param args Command line arguments
     */
    public static void main(final String[] args) throws Exception {
    	Map<String, String> searchQuery = new HashMap<String, String>();
    	List<JsonObject> totalitems=new ArrayList<JsonObject>();
    	
        final Github github = new RtGithub("nafeesiqbal346@gmail.com","Nafees001..@");
		int numberofpage=10; // if int doesn't work then change it to final int and vice-versa.
        final String baselocalpath="D:\\Researh_Works\\data\\";
        CloneRemoteRepository gitremoterepo=new CloneRemoteRepository();
		File file = new File("D:\\Output.csv");
		FileWriter outputfile = new FileWriter(file);
		CSVWriter writer = new CSVWriter(outputfile);
		String[] header = { "CLoneUrl", "Repo", "Topic" };
		writer.writeNext(header);
        
        for(int i=1;i<=numberofpage;i++)
        {
        
        	searchQuery.put("q","VR language:C#");
        	//searchQuery.put("l","java");
        	//searchQuery.put("type","Repositories");
        	searchQuery.put("sort","stars");
        	searchQuery.put("order","desc");
        	//It can show maximum 100 objects per page. So value is set to 100
        	searchQuery.put("per_page", "100");
        	searchQuery.put("page", Integer.toString(i));  
              
        	final JsonResponse resp = github.entry()
        			.uri().path("/search/repositories")
        			.queryParams( searchQuery).back()
        			.fetch()
        			.as(JsonResponse.class);
        
               
         
        	final List<JsonObject> items = resp.json().readObject()
        			.getJsonArray("items")
        			.getValuesAs(JsonObject.class);
        	
        	
        	for (final JsonObject item : items) {
        		totalitems.add(item);        		
        	}
        }
        	
        
       //Sample code to print jsonobjects      
       /* int count=1;
        for (final JsonObject item :totalitems) {
        	System.out.println( count ) ;
        	count++;
        	System.out.print(item);
            System.out.println(
                String.format(
                    "repository found: %s",
                    item.get("full_name").toString()
                )
            );
        }*/
        //end print code.
        
        //Code to call git clone of the repos
        for (final JsonObject item :totalitems) 
        {
        	 JsonObject obj=item;
        	 String cloneurl= obj.getString("clone_url");
        	 String reponame= obj.getString("name");   
        	 JsonArray topic=obj.getJsonArray("topics");
        	 String array=topic.toString();

        	 
        	 if(!array.contains("interview") || !array.contains("class") || !array.contains("assignment"))
        	 {
        		 System.out.println(cloneurl);    
        		 System.out.println(reponame); 
        		 System.out.println(topic);
				 //CSVWriter writer = new CSVWriter(new FileWriter("D:\\Output.csv"));


				 String[] data = {cloneurl,reponame, array};
				 writer.writeNext(data);
				 writer.flush();



        	 }
        	 
        	// String localpath=baselocalpath+reponame;
        	 
        	//gitremoterepo.DownloadRemoteRepository(localpath, cloneurl);

        }  
       
      
    }

}
