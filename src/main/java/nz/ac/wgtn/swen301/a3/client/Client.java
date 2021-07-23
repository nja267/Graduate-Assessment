package nz.ac.wgtn.swen301.a3.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

/**
 * Standalone program that takes input from the command line when the generated jar file is run.
 */
public class Client {

    public static void main(String[] args){
        //checking if the user has input the file type and name in the command line
        if(args[0] != null && args[1] != null){

            //getting the type and the fileName given from the command line
            String type = args[0];
            String fileName = args[1];

            //creating a new HTTP Client
            var client = HttpClient.newHttpClient();

            //if the type is excel (xls), create the appropriate file with the content
            if(type.equals("excel")){
                try{
                    var request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create("http://localhost:8080/resthome4logs/statsxls"))
                            .setHeader("Content-Disposition", "attachment;filename=" + fileName)
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));
                } catch(Exception e){
                    //otherwise print error message
                    System.out.println("THE SERVER IS NOT AVAILABLE!!!");
                }
            } else if(type.equals("csv")){ //if the type is csv, create the appropriate file with the content
                try{
                    var request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create("http://localhost:8080/resthome4logs/statscsv"))
                            .setHeader("Content-Disposition", "attachment;filename=" + fileName)
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));
                } catch(Exception e){
                    //otherwise print error message
                    System.out.println("THE SERVER IS NOT AVAILABLE!!!");
                }
            }

        }
    }
}
