package com.stonecastle.scheduler;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.quartz.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jcardali
 * Date: 8/6/15
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class CamelQuartz {

    private CamelContext context = null;
    private static ProducerTemplate template = null;

    public CamelQuartz () {
        this.context = new DefaultCamelContext();
        this.template = new DefaultProducerTemplate(this.context);
    };

    public static void main(String[] args)throws Exception {
        CamelQuartz cq = new CamelQuartz();
        cq.addRouteWrapper("file:test1", "file:test2");
        boolean stop = false;
        int count = 0;
        File f;
        BufferedWriter bw;

        try {
            cq.context.start();
            cq.template.start();
        } catch (Exception e) {
            System.out.println("Context failed to start");
            e.printStackTrace();
        }
        System.out.println("Context started");
        while(!stop){
            String fname = "C:\\Users\\jcardali\\IdeaProjects\\sc-scheduler\\test1\\%s.txt";
            System.out.println(String.format(fname, count));
            f = new File(String.format(fname, count));
            bw = new BufferedWriter(new FileWriter(f));
            bw.write("Hello world #" + count + " !");
            bw.close();
            if (Math.random() < 0.001) { stop = !stop; }
            count++;
            //template.sendBody("file:test1", new File(i + ".txt"));
        }
        Thread.sleep(10000);
        cq.context.stop();
        System.out.println("Context stopped");
    }

    private void addRouteWrapper(final String from, final String to){
        try{
        this.context.addRoutes(new RouteBuilder() {
            @Override
            public void configure () {
                from(from).to(to);
            }
        });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String buildPGEventString(String hostname, int port, String db, String channel, String user, String pass){
        return String.format("pg:event://%s:%d/%s/%s[?user=%s&pass=", hostname, port, db, channel, user, pass);
    }

}
