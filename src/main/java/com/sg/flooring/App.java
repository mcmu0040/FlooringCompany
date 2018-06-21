/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring;

import com.sg.flooring.controller.FlooringController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author mcmu0
 */
public class App {

    public static void main(String[] args) {
//        UserIO io = new SimpleIO();
//        FlooringViewer view = new FlooringViewer(io);
//        FlooringDao dao = new FlooringDaoImpl();
//        FlooringService service = new FlooringService(dao);
//        FlooringController controller = new FlooringController(service, view);

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        FlooringController controller = ctx.getBean("controller", FlooringController.class);

        controller.run();
    }
}
