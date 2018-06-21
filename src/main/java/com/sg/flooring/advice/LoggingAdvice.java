/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.advice;

import com.sg.flooring.dao.FlooringAuditDao;
import com.sg.flooring.dao.FlooringDaoException;
import org.aspectj.lang.JoinPoint;

/**
 *
 * @author mcmu0
 */
public class LoggingAdvice {
    
   FlooringAuditDao auditDao;

    public LoggingAdvice(FlooringAuditDao auditDao) {
        this.auditDao = auditDao;
    }
   
   public void createAuditEntry(JoinPoint jp) {
        Object[] args = jp.getArgs();
        String auditEntry = jp.getSignature().getName() + ": ";
        for (Object currentArg : args) {
            auditEntry += currentArg + ", ";
        }
        try {
            auditDao.writeAuditEntry(auditEntry);
        } catch (FlooringDaoException e) {
            System.err.println("ERROR: Could not create audit entry in LoggingAdvice.");
        }
    }
}
